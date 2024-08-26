package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.InlineCompletionSuggestion
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vcs.VcsException
import com.intellij.refactoring.suggested.startOffset
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService
import ee.carlrobert.codegpt.codecompletions.psi.readText
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.ui.OverlayUtil.showNotification
import ee.carlrobert.codegpt.util.GitUtil
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference

class CodeGPTInlineCompletionSuggestion(
    private val project: Project,
    private val inlineCompletionRequest: InlineCompletionRequest
) : InlineCompletionSuggestion() {

    private val currentCall = AtomicReference<EventSource>(null)

    companion object {
        private val logger = thisLogger()
        private const val MAX_PROMPT_TOKENS = 128
    }

    override val suggestionFlow: Flow<InlineCompletionElement>
        get() = channelFlow {
            val infillRequest = buildInfillRequest(inlineCompletionRequest)
            launch {
                val completionCall =
                    project.service<CodeCompletionService>().getCodeCompletionAsync(
                        infillRequest,
                        CodeCompletionEventListener {
                            try {
                                runInEdt {
                                    trySend(InlineCompletionGrayTextElement(it.toString()))
                                }
                            } catch (e: Exception) {
                                logger.error("Failed to send inline completion suggestion", e)
                            }
                        }
                    )
                currentCall.set(completionCall)
            }
            awaitClose { currentCall.getAndSet(null)?.cancel() }
        }

    private class CodeCompletionEventListener(
        private val completed: (StringBuilder) -> Unit
    ) : CompletionEventListener<String> {

        override fun onComplete(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onCancelled(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onError(error: ErrorDetails, ex: Throwable) {
            if (ex.message == null || (ex.message != null && ex.message != "Canceled")) {
                showNotification(error.message, NotificationType.ERROR)
                logger.error(error.message, ex)
            }
        }
    }

    private suspend fun buildInfillRequest(request: InlineCompletionRequest): InfillRequest {
        val caretOffset = withContext(Dispatchers.EDT) { request.editor.caretModel.offset }
        val configurationState = service<ConfigurationSettings>().state
        val (prefix, suffix) = withContext(Dispatchers.EDT) {
            val prefix =
                request.document.getText(TextRange(0, caretOffset))
            val suffix =
                request.document.getText(
                    TextRange(
                        caretOffset,
                        request.document.textLength
                    )
                )
            Pair(
                prefix.truncateText(MAX_PROMPT_TOKENS, false),
                suffix.truncateText(MAX_PROMPT_TOKENS)
            )
        }
        val fileExtension = request.file.virtualFile.extension
        val fileContent = request.document.text
        val infillRequestBuilder = InfillRequest.Builder(prefix, suffix)
            .fileDetails(InfillRequest.FileDetails(fileContent, fileExtension))
        val project = request.editor.project ?: return infillRequestBuilder.build()

        val gitRepository =
            project.service<GitRepositoryManager>().getRepositoryForFile(project.workspaceFile)
        if (configurationState.autocompletionGitContextEnabled && gitRepository != null) {
            try {
                val stagedDiff = GitUtil.getStagedDiff(project, gitRepository)
                val unstagedDiff = GitUtil.getUnstagedDiff(project, gitRepository)
                if (stagedDiff.isNotEmpty() || unstagedDiff.isNotEmpty()) {
                    infillRequestBuilder.vcsDetails(
                        InfillRequest.VcsDetails(
                            stagedDiff.joinToString("\n"),
                            unstagedDiff.joinToString("\n")
                        )
                    )
                }
            } catch (e: VcsException) {
                logger.error("Failed to get git context", e)
            }
        }

        getInfillContext(request, caretOffset)?.let { infillRequestBuilder.context(it) }

        return infillRequestBuilder.build()
    }

    private fun getInfillContext(
        request: InlineCompletionRequest,
        caretOffset: Int
    ): InfillContext? {
        val infillContext =
            if (service<ConfigurationSettings>().state.autocompletionContextAwareEnabled)
                service<CompletionContextService>().findContext(request.editor, caretOffset)
            else null

        if (infillContext == null) {
            return null
        }

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

    private fun String.truncateText(maxTokens: Int, fromStart: Boolean = true): String {
        return service<EncodingManager>().truncateText(this, maxTokens, fromStart)
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