package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.Logger
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.llm.completion.CompletionEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference

class CodeGPTInlineCompletionProvider : InlineCompletionProvider {

    companion object {
        private val LOG = Logger.getInstance(CodeGPTInlineCompletionProvider::class.java)
    }

    private val currentCall = AtomicReference<EventSource>(null)

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
            currentCall.set(
                CompletionRequestService.getInstance().getCodeCompletionAsync(
                    infillRequest,
                    CodeCompletionEventListener {
                        val inlineText = it.takeWhile { message -> message != '\n' }.toString()
                        request.editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, inlineText)
                        launch {
                            try {
                                trySend(InlineCompletionGrayTextElement(inlineText))
                            } catch (e: Exception) {
                                LOG.error("Failed to send inline completion suggestion", e)
                            }
                        }
                    }
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

    private fun cancelCurrentCall() {
        currentCall.getAndSet(null)?.cancel()
    }

    class CodeCompletionEventListener(
        private val completed: (StringBuilder) -> Unit
    ) : CompletionEventListener<String> {

        override fun onMessage(message: String?, eventSource: EventSource?) {
            if (message != null && message.contains('\n')) {
                eventSource?.cancel()
            }
        }

        override fun onComplete(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onCancelled(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }
    }
}