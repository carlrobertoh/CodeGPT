package ee.carlrobert.codegpt.settings.service.ollama

import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.service.CodeCompletionConfigurationForm
import ee.carlrobert.codegpt.ui.UIUtil

class OllamaSettingsForm(settings: OllamaSettingsState) {

    private val hostField: JBTextField = JBTextField()
    private val modelField: JBTextField = JBTextField()
    private val codeCompletionConfigurationForm: CodeCompletionConfigurationForm =
        CodeCompletionConfigurationForm(
            settings.isCodeCompletionsEnabled,
            settings.codeCompletionMaxTokens
        )

    fun getForm() = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator(CodeGPTBundle.get("shared.configuration")))
        .addComponent(
            FormBuilder.createFormBuilder()
                .setFormLeftIndent(16)
                .addLabeledComponent(
                    CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"),
                    hostField
                )
                .addLabeledComponent(
                    CodeGPTBundle.get("settingsConfigurable.shared.model.label"),
                    modelField
                )
                .panel
        )
        .addComponent(TitledSeparator(CodeGPTBundle.get("shared.codeCompletions")))
        .addComponent(UIUtil.withEmptyLeftBorder(codeCompletionConfigurationForm.getForm()))
        .panel

    fun getHost(): String {
        return hostField.text
    }

    fun getModel(): String {
        return modelField.text
    }

    fun getCurrentState(): OllamaSettingsState {
        val state = OllamaSettingsState()
        state.host = getHost()
        state.model = getModel()
        state.isCodeCompletionsEnabled = codeCompletionConfigurationForm.isCodeCompletionsEnabled
        state.codeCompletionMaxTokens = codeCompletionConfigurationForm.maxTokens
        return state
    }

    fun resetForm() {
        val state = OllamaSettings.getCurrentState()
        hostField.text = state.host
        modelField.text = state.model
        codeCompletionConfigurationForm.isCodeCompletionsEnabled = state.isCodeCompletionsEnabled
        codeCompletionConfigurationForm.maxTokens = state.codeCompletionMaxTokens
    }
}