package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSingleSuggestion
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestion
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettings
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DebouncedCodeCompletionProvider : DebouncedInlineCompletionProvider() {

    companion object {
        private val logger = thisLogger()
    }

    private val currentCallRef = AtomicReference<EventSource?>(null)

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override val suggestionUpdateManager: CodeCompletionSuggestionUpdateAdapter
        get() = CodeCompletionSuggestionUpdateAdapter()

    override val insertHandler: InlineCompletionInsertHandler
        get() = CodeCompletionInsertHandler()

    override val providerPresentation: InlineCompletionProviderPresentation
        get() = CodeCompletionProviderPresentation()

    override suspend fun getSuggestionDebounced(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val editor = request.editor
        val remainingCompletion = REMAINING_EDITOR_COMPLETION.get(editor)
        if (request.event is InlineCompletionEvent.DirectCall
            && remainingCompletion != null
            && remainingCompletion.isNotEmpty()
        ) {
            return sendNextSuggestion(remainingCompletion)
        }

        val project = editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSingleSuggestion.build(elements = emptyFlow())
        }

        return InlineCompletionSingleSuggestion.build(elements = channelFlow {
            REMAINING_EDITOR_COMPLETION.set(request, "")
            IS_FETCHING_COMPLETION.set(request.editor, true)

            request.editor.project?.messageBus
                ?.syncPublisher(CodeCompletionProgressNotifier.CODE_COMPLETION_PROGRESS_TOPIC)
                ?.loading(true)

            val infillRequest = InfillRequestUtil.buildInfillRequest(request)
            val call = project.service<CodeCompletionService>().getCodeCompletionAsync(
                infillRequest,
                getEventListener(request.editor, infillRequest)
            )
            currentCallRef.set(call)
            awaitClose { currentCallRef.getAndSet(null)?.cancel() }
        })
    }

    override suspend fun getDebounceDelay(request: InlineCompletionRequest): Duration {
        return 600.toDuration(DurationUnit.MILLISECONDS)
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        val selectedService = GeneralSettings.getSelectedService()
        val codeCompletionsEnabled = when (selectedService) {
            ServiceType.CODEGPT -> service<CodeGPTServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            ServiceType.OPENAI -> OpenAISettings.getCurrentState().isCodeCompletionsEnabled
            ServiceType.CUSTOM_OPENAI -> service<CustomServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            ServiceType.LLAMA_CPP -> LlamaSettings.isCodeCompletionsPossible()
            ServiceType.OLLAMA -> service<OllamaSettings>().state.codeCompletionsEnabled
            ServiceType.WATSONX -> WatsonxSettings.isCodeCompletionsPossible()
            ServiceType.ANTHROPIC,
            ServiceType.AZURE,
            ServiceType.GOOGLE,
            null -> false
        }
        val containsActiveCompletion =
            REMAINING_EDITOR_COMPLETION.get(event.toRequest()?.editor)?.isNotEmpty() ?: false

        return (event is InlineCompletionEvent.DocumentChange && codeCompletionsEnabled)
                || containsActiveCompletion
    }

    private fun ProducerScope<InlineCompletionElement>.getEventListener(
        editor: Editor,
        infillRequest: InfillRequest
    ) = object : CodeCompletionCompletionEventListener(editor, infillRequest) {
        override fun onMessage(message: String) {
            runInEdt {
                trySend(InlineCompletionGrayTextElement(message))
            }
        }

        override fun onComplete(fullMessage: String) {
            REMAINING_EDITOR_COMPLETION.set(editor, fullMessage)
        }
    }

    private fun sendNextSuggestion(nextCompletion: String): InlineCompletionSingleSuggestion {
        return InlineCompletionSingleSuggestion.build(elements = channelFlow {
            launch {
                trySend(
                    InlineCompletionGrayTextElement(
                        nextCompletion.lines().take(2).joinToString("\n")
                    )
                )
            }
        })
    }
}
