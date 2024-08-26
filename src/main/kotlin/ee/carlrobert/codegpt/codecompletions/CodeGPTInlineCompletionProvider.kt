package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CodeGPTInlineCompletionProvider : DebouncedInlineCompletionProvider() {

    companion object {
        private val logger = thisLogger()
    }

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override suspend fun getSuggestionDebounced(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val editor = request.editor
        val project = editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSuggestion.empty()
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
}