package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.treesitter.CodeCompletionParserFactory
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
            val infillRequest = withContext(Dispatchers.EDT) {
                InfillRequestDetails.fromInlineCompletionRequest(request)
            }
            cancelCurrentCall()
            currentCall.set(
                CompletionRequestService.getInstance().getCodeCompletionAsync(
                    infillRequest,
                    getCodeCompletionEventListener(request.editor, infillRequest)
                )
            )
            awaitClose { cancelCurrentCall() }
        })
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        val selectedService = GeneralSettings.getCurrentState().selectedService
        val codeCompletionsEnabled = when (selectedService) {
            ServiceType.OPENAI -> OpenAISettings.getCurrentState().isCodeCompletionsEnabled
            ServiceType.LLAMA_CPP -> LlamaSettings.getCurrentState().isCodeCompletionsEnabled
            else -> false
        }
        return event is InlineCompletionEvent.DocumentChange && codeCompletionsEnabled
    }

    private fun ProducerScope<InlineCompletionElement>.getCodeCompletionEventListener(
        editor: Editor,
        infillRequest: InfillRequestDetails
    ) = CodeCompletionEventListener(infillRequest) {
        editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, it)
        providerScope.launch {
            try {
                send(InlineCompletionGrayTextElement(it))
            } catch (e: Exception) {
                LOG.error("Failed to send inline completion suggestion", e)
            }
        }
    }

    private fun cancelCurrentCall() {
        currentCall.getAndSet(null)?.cancel()
    }

    class CodeCompletionEventListener(
        private val requestDetails: InfillRequestDetails,
        private val completed: (String) -> Unit
    ) : CompletionEventListener<String> {

        override fun onComplete(messageBuilder: StringBuilder) {
            val processedOutput = CodeCompletionParserFactory
                .getParserForFileExtension(requestDetails.fileExtension)
                .parse(
                    requestDetails.prefix,
                    requestDetails.suffix,
                    messageBuilder.toString()
                )
            completed(processedOutput)
        }
    }
}