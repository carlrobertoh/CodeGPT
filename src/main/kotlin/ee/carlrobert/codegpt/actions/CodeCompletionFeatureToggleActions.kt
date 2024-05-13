package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import ee.carlrobert.codegpt.codecompletions.CodeCompletionService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType.*
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings

abstract class CodeCompletionFeatureToggleActions(
    private val enableFeatureAction: Boolean
) : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        when (GeneralSettings.getCurrentState().selectedService) {
            CODEGPT ->
                service<CodeGPTServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled =
                    enableFeatureAction

            OPENAI ->
                OpenAISettings.getCurrentState().isCodeCompletionsEnabled = enableFeatureAction

            LLAMA_CPP ->
                LlamaSettings.getCurrentState().isCodeCompletionsEnabled = enableFeatureAction

            OLLAMA -> service<OllamaSettings>().state.codeCompletionsEnabled = enableFeatureAction
            CUSTOM_OPENAI -> service<CustomServiceSettings>().state
                .codeCompletionSettings
                .codeCompletionsEnabled = enableFeatureAction

            ANTHROPIC,
            AZURE,
            YOU,
            GOOGLE,
            null -> { /* no-op for these services */
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val selectedService = GeneralSettings.getCurrentState().selectedService
        val codeCompletionEnabled =
            service<CodeCompletionService>().isCodeCompletionsEnabled(selectedService)
        e.presentation.isVisible = codeCompletionEnabled != enableFeatureAction
        e.presentation.isEnabled = when (selectedService) {
            CODEGPT,
            OPENAI,
            CUSTOM_OPENAI,
            LLAMA_CPP,
            OLLAMA -> true

            ANTHROPIC,
            AZURE,
            YOU,
            GOOGLE,
            null -> false
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}

class EnableCompletionsAction : CodeCompletionFeatureToggleActions(true)

class DisableCompletionsAction : CodeCompletionFeatureToggleActions(false)
