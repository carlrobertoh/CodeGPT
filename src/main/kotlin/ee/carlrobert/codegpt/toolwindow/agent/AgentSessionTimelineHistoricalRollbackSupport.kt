package ee.carlrobert.codegpt.toolwindow.agent

import ai.koog.agents.snapshot.feature.AgentCheckpointData
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import ee.carlrobert.codegpt.agent.ToolName
import ee.carlrobert.codegpt.agent.history.AgentCheckpointHistoryService
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import java.util.*
import ai.koog.prompt.message.Message as PromptMessage

internal class AgentSessionTimelineHistoricalRollbackSupport(
    private val project: Project,
    private val historyService: AgentCheckpointHistoryService,
    private val replayJson: Json
) {

    suspend fun collectOperations(point: RunTimelinePoint): List<HistoricalRollbackOperation> {
        val checkpointRef = point.checkpointRef ?: return emptyList()
        val latestCheckpoint: AgentCheckpointData =
            historyService.listCheckpoints(checkpointRef.agentId).firstOrNull()
                ?: historyService.loadLatestResumeCheckpoint(checkpointRef.agentId)
                ?: historyService.loadCheckpoint(checkpointRef)
                ?: return emptyList()
        val cutoff = point.nonSystemMessageCount ?: return emptyList()
        val history = latestCheckpoint.messageHistory.filterNot { it is PromptMessage.System }
        if (history.isEmpty() || cutoff >= history.size) return emptyList()

        data class PendingCall(val index: Int, val call: ai.koog.prompt.message.MessagePart.Tool.Call)

        val pendingById = mutableMapOf<String, PendingCall>()
        val pendingWithoutId = ArrayDeque<PendingCall>()
        val latestKnownContentByFile = mutableMapOf<String, String>()
        val operations = mutableListOf<HistoricalRollbackOperation>()

        history.forEachIndexed { index, message ->
            if (message is PromptMessage.Assistant) {
                message.parts.filterIsInstance<ai.koog.prompt.message.MessagePart.Tool.Call>().forEach { call ->
                    val callId = call.id?.takeIf { it.isNotBlank() }
                    if (callId != null) {
                        pendingById[callId] = PendingCall(index, call)
                    } else {
                        pendingWithoutId.addLast(PendingCall(index, call))
                    }
                }
            } else if (message is PromptMessage.User) {
                message.parts.filterIsInstance<ai.koog.prompt.message.MessagePart.Tool.Result>().forEach { result ->
                    val pendingCall = result.id
                        ?.takeIf { it.isNotBlank() }
                        ?.let { pendingById.remove(it) }
                        ?: pendingWithoutId.pollFirst()
                        ?: return@forEach
                    val call = pendingCall.call

                    val tool = HistoricalRollbackCompatibility.resolveSupportedTool(call.tool)
                        ?: return@forEach
                        
                    val resultText = result.parts.filterIsInstance<ai.koog.prompt.message.MessagePart.Text>().joinToString("\n") { it.text }
                        
                    if (!HistoricalRollbackCompatibility.isSuccessfulResult(
                            tool,
                            resultText,
                            replayJson
                        )
                    ) {
                        return@forEach
                    }

                    when (tool) {
                        ToolName.READ -> {
                            val args = HistoricalRollbackCompatibility.decodeReadArgs(
                                replayJson = replayJson,
                                rawToolName = call.tool,
                                rawArgs = call.args
                            ) ?: return@forEach
                            val filePath = HistoricalRollbackCompatibility.normalizeToolFilePath(
                                project.basePath,
                                args.filePath
                            )
                                ?: return@forEach
                            val content =
                                HistoricalRollbackCompatibility.decodeReadToolResultContent(
                                    resultText
                                ) ?: return@forEach
                            latestKnownContentByFile[filePath] = content
                            return@forEach
                        }

                        ToolName.EDIT -> {
                            val args = HistoricalRollbackCompatibility.decodeEditArgs(
                                replayJson = replayJson,
                                rawToolName = call.tool,
                                rawArgs = call.args
                            ) ?: return@forEach
                            val filePath = HistoricalRollbackCompatibility.normalizeToolFilePath(
                                project.basePath,
                                args.filePath
                            )
                                ?: return@forEach
                            val oldString = args.oldString
                            val newString = args.newString
                            val replaceAll = args.replaceAll
                            if (oldString.isEmpty() || newString.isEmpty() || oldString == newString) return@forEach

                            if (pendingCall.index >= cutoff) {
                                operations.add(
                                    HistoricalRollbackOperation(
                                        filePath = filePath,
                                        searchText = newString,
                                        replacementText = oldString,
                                        replaceAll = replaceAll,
                                        sourceTool = HistoricalRollbackSourceTool.EDIT
                                    )
                                )
                            }

                            latestKnownContentByFile[filePath]?.let { known ->
                                if (known.contains(oldString)) {
                                    latestKnownContentByFile[filePath] =
                                        if (replaceAll) known.replace(oldString, newString)
                                        else known.replaceFirst(oldString, newString)
                                }
                            }
                            return@forEach
                        }

                        ToolName.WRITE -> {
                            val args = HistoricalRollbackCompatibility.decodeWriteArgs(
                                replayJson = replayJson,
                                rawToolName = call.tool,
                                rawArgs = call.args
                            ) ?: return@forEach
                            val filePath = HistoricalRollbackCompatibility.normalizeToolFilePath(
                                project.basePath,
                                args.filePath
                            )
                                ?: return@forEach
                            val newContent = args.content
                            val previousContent = latestKnownContentByFile[filePath]
                            if (pendingCall.index >= cutoff && previousContent != null && previousContent != newContent) {
                                operations.add(
                                    HistoricalRollbackOperation(
                                        filePath = filePath,
                                        searchText = newContent,
                                        replacementText = previousContent,
                                        replaceAll = false,
                                        sourceTool = HistoricalRollbackSourceTool.WRITE
                                    )
                                )
                            }
                            latestKnownContentByFile[filePath] = newContent
                        }

                        else -> Unit
                    }
                }
            }
        }

        return operations
    }

    fun buildConfirmationText(
        selectedLabel: String,
        operations: List<HistoricalRollbackOperation>
    ): String {
        val ordered = operations.asReversed()
        val previewLimit = 12
        val listed = ordered.take(previewLimit).joinToString(separator = "\n") { operation ->
            "${operation.sourceTool.symbol} ${toProjectRelativePath(operation.filePath)}"
        }
        val remaining = ordered.size - previewLimit
        val suffix = if (remaining > 0) "\n...and $remaining more operation(s)." else ""
        return """
            This rollback will return the session to:
            $selectedLabel

            It will also replay ${operations.size} file operation(s) in reverse order:

            $listed$suffix
        """.trimIndent()
    }

    fun applyOperations(operations: List<HistoricalRollbackOperation>): List<String> {
        val errors = mutableListOf<String>()
        operations.asReversed().forEach { operation ->
            val filePath = operation.filePath
            val currentText = readFileText(filePath)
            if (currentText == null) {
                errors.add("File not found: ${toProjectRelativePath(filePath)}")
                return@forEach
            }
            if (!currentText.contains(operation.searchText)) {
                errors.add(
                    "Expected content not found in ${toProjectRelativePath(filePath)} for ${operation.sourceTool.displayName} rollback."
                )
                return@forEach
            }

            val updatedText = if (operation.replaceAll) {
                currentText.replace(operation.searchText, operation.replacementText)
            } else {
                currentText.replaceFirst(operation.searchText, operation.replacementText)
            }
            if (updatedText == currentText) {
                errors.add("No changes applied to ${toProjectRelativePath(filePath)}")
                return@forEach
            }

            val writeOk = writeFileText(filePath, updatedText)
            if (!writeOk) {
                errors.add("Failed to write ${toProjectRelativePath(filePath)}")
            }
        }
        return errors
    }

    private fun readFileText(path: String): String? {
        val virtualFile =
            LocalFileSystem.getInstance().refreshAndFindFileByPath(path) ?: return null
        val documentText = runReadActionBlocking {
            FileDocumentManager.getInstance().getDocument(virtualFile)?.text
        }
        if (documentText != null) return documentText
        return runCatching { String(virtualFile.contentsToByteArray(), Charsets.UTF_8) }.getOrNull()
    }

    private fun writeFileText(path: String, content: String): Boolean {
        val virtualFile =
            LocalFileSystem.getInstance().refreshAndFindFileByPath(path) ?: return false
        val document =
            runReadActionBlocking { FileDocumentManager.getInstance().getDocument(virtualFile) }
        return runCatching {
            runInEdt {
                runWriteAction {
                    if (document != null) {
                        document.setText(content)
                        FileDocumentManager.getInstance().saveDocument(document)
                    } else {
                        virtualFile.setBinaryContent(content.toByteArray(Charsets.UTF_8))
                    }
                }
            }
        }.isSuccess
    }

    private fun toProjectRelativePath(path: String): String {
        val basePath = project.basePath ?: return path
        return runCatching {
            val absolute = Paths.get(path).normalize()
            val base = Paths.get(basePath).normalize()
            if (absolute.startsWith(base)) {
                base.relativize(absolute).toString().replace("\\", "/")
            } else {
                path
            }
        }.getOrDefault(path)
    }
}
