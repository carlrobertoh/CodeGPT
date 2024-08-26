package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.DebouncedInlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionProviderID
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSingleSuggestion
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestion
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Changed
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Invalidated
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionVariant
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import kotlinx.coroutines.flow.emptyFlow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CodeGPTInlineCompletionProvider : DebouncedInlineCompletionProvider() {

    companion object {
        private val logger = thisLogger()
    }

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override val suggestionUpdateManager: CodeCompletionSuggestionUpdateAdapter
        get() = CodeCompletionSuggestionUpdateAdapter()

    override suspend fun getSuggestionDebounced(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val editor = request.editor
        val project = editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSingleSuggestion.build(elements = emptyFlow())
        }
        return CodeGPTInlineCompletionSuggestion(project, request)
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
            ServiceType.ANTHROPIC,
            ServiceType.AZURE,
            ServiceType.GOOGLE,
            null -> false
        }
        return event is InlineCompletionEvent.DocumentChange && codeCompletionsEnabled
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

