package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.treesitter.CodeCompletionParserFactory
import ee.carlrobert.codegpt.util.file.FileUtil
import ee.carlrobert.llm.completion.CompletionEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference

class CodeGPTInlineCompletionProvider : InlineCompletionProvider {

    companion object {
        private val LOG = Logger.getInstance(CodeGPTInlineCompletionProvider::class.java)
    }

    private val currentCall = AtomicReference<EventSource>(null)
    private val providerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSuggestion {
        if (request.editor.project == null) {
            LOG.error("Could not find project")
            return InlineCompletionSuggestion.empty()
        }

        return InlineCompletionSuggestion.Default(channelFlow {
            val (infillRequest, fileExtension) = withContext(Dispatchers.EDT) {
                val infillRequest = InfillRequestDetails.fromCurrentEditorCaret(request.editor)
                val fileExtension = FileUtil.getFileExtension(request.file.name)
                Pair(infillRequest, fileExtension)
            }
            cancelCurrentCall()
            currentCall.set(
                getCodeCompletion(
                    infillRequest,
                    getCodeCompletionEventListener(request.editor, infillRequest, fileExtension)
                )
            )
            awaitClose { cancelCurrentCall() }
        })
    }

    private fun ProducerScope<InlineCompletionElement>.getCodeCompletionEventListener(
        editor: Editor,
        infillRequest: InfillRequestDetails,
        fileExtension: String
    ) = CodeCompletionEventListener(infillRequest, fileExtension) {
        editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, it)
        providerScope.launch {
            try {
                send(InlineCompletionGrayTextElement(it))
            } catch (e: Exception) {
                LOG.error("Failed to send inline completion suggestion", e)
            }
        }
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        return ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled
    }

    private fun getCodeCompletion(
        request: InfillRequestDetails,
        eventListener: CodeCompletionEventListener,
    ): EventSource {
        return CompletionRequestService.getInstance().getCodeCompletionAsync(request, eventListener)
    }

    private fun cancelCurrentCall() {
        currentCall.getAndSet(null)?.cancel()
    }

    class CodeCompletionEventListener(
        private val requestDetails: InfillRequestDetails,
        private val fileExtension: String,
        private val completed: (String) -> Unit
    ) : CompletionEventListener<String> {

        override fun onComplete(messageBuilder: StringBuilder) {
            val processedOutput = CodeCompletionParserFactory
                .getParserForFileExtension(fileExtension)
                .parse(
                    requestDetails.prefix,
                    requestDetails.suffix,
                    messageBuilder.toString()
                )
            completed(processedOutput)
        }
    }
}