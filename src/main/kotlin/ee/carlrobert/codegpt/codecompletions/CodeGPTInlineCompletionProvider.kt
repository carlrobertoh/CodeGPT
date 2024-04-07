package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionProviderID
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSingleSuggestion
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Changed
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Invalidated
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionVariant
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.Logger
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.treesitter.CodeCompletionParserFactory
import ee.carlrobert.llm.completion.CompletionEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
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

    override val suggestionUpdateManager: CodeCompletionSuggestionUpdateAdapter
        get() = CodeCompletionSuggestionUpdateAdapter()

    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSingleSuggestion {
        if (request.editor.project == null) {
            LOG.error("Could not find project")
            return InlineCompletionSingleSuggestion.build(elements = emptyFlow())
        }

        return InlineCompletionSingleSuggestion.build(elements = channelFlow {
            val infillRequest = withContext(Dispatchers.EDT) {
                InfillRequestDetails.fromInlineCompletionRequest(request)
            }
            currentCall.set(
                CompletionRequestService.getInstance().getCodeCompletionAsync(
                    infillRequest,
                    CodeCompletionEventListener(infillRequest) {
                        launch {
                            try {
                                trySend(InlineCompletionGrayTextElement(it))
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

    internal class CodeCompletionEventListener(
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

    class CodeCompletionSuggestionUpdateAdapter :
        InlineCompletionSuggestionUpdateManager.Default() {

        override fun onCustomEvent(
            event: InlineCompletionEvent,
            variant: InlineCompletionVariant.Snapshot
        ): UpdateResult {
            if (event !is ApplyNextWordInlaySuggestionEvent || variant.elements.isEmpty()) {
                return Invalidated
            }

            val completionText = variant.elements.firstOrNull()?.text ?: return Invalidated
            val textToInsert = event.toRequest().run {
                CompletionSplitter.split(completionText)
            }
            return Changed(
                variant.copy(
                    listOf(InlineCompletionGrayTextElement(completionText.removePrefix(textToInsert)))
                )
            )
        }
    }
}

