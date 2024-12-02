package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.diff.impl.patch.IdeaTextPatchBuilder
import com.intellij.openapi.diff.impl.patch.UnifiedDiffWriter
import com.intellij.openapi.vcs.VcsException
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.refactoring.suggested.startOffset
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService
import ee.carlrobert.codegpt.codecompletions.psi.readText
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.util.GitUtil
import java.io.StringWriter

object InfillRequestUtil {
    private val logger = thisLogger()

    suspend fun buildInfillRequest(
        request: InlineCompletionRequest,
        type: CompletionType
    ): InfillRequest {
        val caretOffset = readAction { request.editor.caretModel.offset }
        val infillRequestBuilder = InfillRequest.Builder(request.document, caretOffset, type)
            .fileDetails(
                InfillRequest.FileDetails(
                    request.document.text,
                    request.file.virtualFile.extension
                )
            )

        val project = request.editor.project ?: return infillRequestBuilder.build()
        if (service<ConfigurationSettings>().state.codeCompletionSettings.gitDiffEnabled) {
            GitUtil.getProjectRepository(project)?.let { repository ->
                try {
                    val repoRootPath = repository.root.toNioPath()
                    val changes = ChangeListManager.getInstance(project).allChanges
                        .sortedBy { it.virtualFile?.timeStamp }
                    val patches = IdeaTextPatchBuilder.buildPatch(
                        project, changes, repoRootPath, false, true
                    )
                    val diffWriter = StringWriter()
                    UnifiedDiffWriter.write(
                        null, repoRootPath, patches, diffWriter, "\n\n", null, null
                    )
                    val additionalContext =
                        diffWriter.toString().cleanDiff().truncateText(1024, false)
                    infillRequestBuilder.additionalContext(additionalContext)
                } catch (e: VcsException) {
                    logger.error("Failed to get git context", e)
                }
            }
        }

        if (service<ConfigurationSettings>().state.codeCompletionSettings.contextAwareEnabled) {
            getInfillContext(request, caretOffset)?.let { infillRequestBuilder.context(it) }
        }

        return infillRequestBuilder.build()
    }

    private fun String.cleanDiff(showContext: Boolean = false): String =
        lineSequence()
            .filterNot { line ->
                line.startsWith("index ") ||
                        line.startsWith("diff --git") ||
                        line.startsWith("---") ||
                        line.startsWith("+++") ||
                        line.startsWith("===") ||
                        (!showContext && line.startsWith(" "))
            }
            .joinToString("\n")

    private fun getInfillContext(
        request: InlineCompletionRequest,
        caretOffset: Int
    ): InfillContext? {
        val infillContext =
            service<CompletionContextService>().findContext(request.editor, caretOffset)
                ?: return null
        val caretInEnclosingElement =
            caretOffset - infillContext.enclosingElement.psiElement.startOffset
        val entireText = infillContext.enclosingElement.psiElement.readText()
        val prefix = entireText.take(caretInEnclosingElement)
        val suffix =
            if (entireText.length < caretInEnclosingElement) "" else entireText.takeLast(
                entireText.length - caretInEnclosingElement
            )
        return truncateContext(prefix + suffix, infillContext)
    }

    private fun truncateContext(prompt: String, infillContext: InfillContext): InfillContext {
        var promptTokens = EncodingManager.getInstance().countTokens(prompt)
        val truncatedContextElements = infillContext.contextElements.takeWhile {
            promptTokens += it.tokens
            promptTokens <= MAX_PROMPT_TOKENS
        }.toSet()
        return InfillContext(infillContext.enclosingElement, truncatedContextElements)
    }
}