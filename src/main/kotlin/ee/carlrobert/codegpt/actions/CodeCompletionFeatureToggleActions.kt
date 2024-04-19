package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.ServiceType.*
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings

abstract class CodeCompletionFeatureToggleActions(
    private val enableFeatureAction: Boolean
) : DumbAwareAction() {


    override fun actionPerformed(e: AnActionEvent) {
        GeneralSettings.getCurrentState().selectedService
            .takeIf { it in listOf(OPENAI, CUSTOM_OPENAI, LLAMA_CPP) }
            ?.also { selectedService ->
                if (OPENAI == selectedService) {
                    OpenAISettings.getCurrentState().isCodeCompletionsEnabled = enableFeatureAction
                } else if (CUSTOM_OPENAI == selectedService) {
                    service<CustomServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled =
                        enableFeatureAction
                } else {
                    LlamaSettings.getCurrentState().isCodeCompletionsEnabled = enableFeatureAction
                }
            }
    }

    override fun update(e: AnActionEvent) {
        val selectedService = GeneralSettings.getCurrentState().selectedService
        val codeCompletionEnabled = isCodeCompletionsEnabled(selectedService)
        e.presentation.isEnabled = codeCompletionEnabled != enableFeatureAction
        e.presentation.isVisible =
            e.presentation.isEnabled && listOf(OPENAI, CUSTOM_OPENAI, LLAMA_CPP).contains(
                selectedService
            )
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun isCodeCompletionsEnabled(serviceType: ServiceType): Boolean {
        return when (serviceType) {
            OPENAI -> OpenAISettings.getCurrentState().isCodeCompletionsEnabled
            CUSTOM_OPENAI -> service<CustomServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            LLAMA_CPP -> LlamaSettings.getCurrentState().isCodeCompletionsEnabled
            else -> false
        }
    }
}

class EnableCompletionsAction : CodeCompletionFeatureToggleActions(true)

class DisableCompletionsAction : CodeCompletionFeatureToggleActions(false)