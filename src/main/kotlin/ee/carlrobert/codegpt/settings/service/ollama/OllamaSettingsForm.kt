package ee.carlrobert.codegpt.settings.service.ollama

import com.intellij.openapi.observable.util.whenTextChangedFromUi
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.service.CodeCompletionConfigurationForm
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.llm.client.ollama.OllamaClient
import java.awt.BorderLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JPanel

class OllamaSettingsForm(settings: OllamaSettingsState) {

    private val needsRefreshModelsComboBox = DefaultComboBoxModel(arrayOf("Hit refresh to see models for this host"))
    private val modelComboBox: ComboBox<String> = ComboBox(needsRefreshModelsComboBox).apply {
        isEnabled = false
    }
    private val refreshModelsButton = JButton(CodeGPTBundle.get("settingsConfigurable.service.ollama.models.refresh"))

    private val hostField: JBTextField = JBTextField()
    private val codeCompletionConfigurationForm: CodeCompletionConfigurationForm =
        CodeCompletionConfigurationForm(
            settings.isCodeCompletionPossible(),
            settings.codeCompletionMaxTokens
        )

    init {
        hostField.whenTextChangedFromUi {
            modelComboBox.model = needsRefreshModelsComboBox
            modelComboBox.isEnabled = false
        }
        refreshModelsButton.addActionListener { refreshModels() }
    }

    fun refreshModels() {
        Thread {
            modelComboBox.apply {
                model = DefaultComboBoxModel(arrayOf("Loading"))
                isEnabled = false

                val models = OllamaClient.Builder()
                    .setHost(hostField.text)
                    .build()
                    .modelTags
                    .models
                    .map { it.name }

                if (models.isNotEmpty()) {
                    model = DefaultComboBoxModel(models.toTypedArray())
                    isEnabled = true
                } else {
                    model = DefaultComboBoxModel(arrayOf("No models"))
                }
            }
        }.start()
    }

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
                    JPanel(BorderLayout(8, 0)).apply {
                        add(modelComboBox, BorderLayout.CENTER)
                        add(refreshModelsButton, BorderLayout.EAST)
                    }
                )
                .panel
        )
        .addComponent(TitledSeparator(CodeGPTBundle.get("shared.codeCompletions")))
        .addComponent(UIUtil.withEmptyLeftBorder(codeCompletionConfigurationForm.getForm()))
        .panel

    fun getCurrentState(): OllamaSettingsState {
        return OllamaSettingsState(
            host = hostField.text,
            model = modelComboBox.item,
            codeCompletionMaxTokens = codeCompletionConfigurationForm.maxTokens,
            codeCompletionsEnabled = codeCompletionConfigurationForm.isCodeCompletionsEnabled
        )
    }

    fun resetForm() {
        val state = OllamaSettings.getCurrentState()
        hostField.text = state.host
        modelComboBox.item = state.model
        codeCompletionConfigurationForm.isCodeCompletionsEnabled = state.isCodeCompletionPossible()
        codeCompletionConfigurationForm.maxTokens = state.codeCompletionMaxTokens

        refreshModels()
    }
}
