package ee.carlrobert.codegpt.settings.service.ollama

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.observable.util.whenTextChangedFromUi
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.service.CodeCompletionConfigurationForm
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.codegpt.ui.URLTextField
import ee.carlrobert.llm.client.ollama.OllamaClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.net.ConnectException
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JPanel

class OllamaSettingsForm {

    private val refreshModelsButton =
        JButton(CodeGPTBundle.get("settingsConfigurable.service.ollama.models.refresh"))
    private val hostField: JBTextField
    private val modelComboBox: ComboBox<String>
    private val codeCompletionConfigurationForm: CodeCompletionConfigurationForm

    companion object {
        private val logger = thisLogger()
    }

    init {
        val settings = service<OllamaSettings>().state
        codeCompletionConfigurationForm = CodeCompletionConfigurationForm(
            settings.codeCompletionsEnabled,
            settings.fimTemplate
        )
        val emptyModelsComboBoxModel =
            DefaultComboBoxModel(arrayOf("Hit refresh to see models for this host"))
        modelComboBox = ComboBox(emptyModelsComboBoxModel).apply {
            isEnabled = false
        }
        hostField = URLTextField().apply {
            text = settings.host
            whenTextChangedFromUi {
                modelComboBox.model = emptyModelsComboBoxModel
                modelComboBox.isEnabled = false
            }
        }
        refreshModelsButton.addActionListener { refreshModels() }
    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
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
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getModel(): String {
        return if (modelComboBox.isEnabled) {
            modelComboBox.item
        } else {
            ""
        }
    }

    fun resetForm() {
        service<OllamaSettings>().state.run {
            hostField.text = host
            modelComboBox.item = model
            codeCompletionConfigurationForm.isCodeCompletionsEnabled = codeCompletionsEnabled
            codeCompletionConfigurationForm.fimTemplate = fimTemplate
        }
    }

    fun applyChanges() {
        service<OllamaSettings>().state.run {
            host = hostField.text
            model = modelComboBox.item
            codeCompletionsEnabled = codeCompletionConfigurationForm.isCodeCompletionsEnabled
            fimTemplate = codeCompletionConfigurationForm.fimTemplate!!
        }
    }

    fun isModified() = service<OllamaSettings>().state.run {
        hostField.text != host
                || modelComboBox.item != model
                || codeCompletionConfigurationForm.isCodeCompletionsEnabled != codeCompletionsEnabled
                || codeCompletionConfigurationForm.fimTemplate != fimTemplate
    }

    private fun refreshModels() {
        disableModelComboBoxWithPlaceholder(DefaultComboBoxModel(arrayOf("Loading")))
        try {
            val models = runBlocking(Dispatchers.IO) {
                OllamaClient.Builder()
                    .setHost(hostField.text)
                    .build()
                    .modelTags
                    .models
                    .map { it.name }
            }
            service<OllamaSettings>().state.availableModels = models.toMutableList()
            invokeLater {
                modelComboBox.apply {
                    if (models.isNotEmpty()) {
                        model = DefaultComboBoxModel(models.toTypedArray())
                        isEnabled = true
                    } else {
                        model = DefaultComboBoxModel(arrayOf("No models"))
                    }
                }
            }
        } catch (ex: RuntimeException) {
            logger.error(ex)
            if (ex.cause is ConnectException) {
                OverlayUtil.showNotification(
                    "Unable to connect to Ollama server",
                    NotificationType.ERROR
                )
            } else {
                OverlayUtil.showNotification(ex.message ?: "Error", NotificationType.ERROR)
            }
            disableModelComboBoxWithPlaceholder(DefaultComboBoxModel(arrayOf("Unable to load models")))
        }
    }

    private fun disableModelComboBoxWithPlaceholder(placeholderModel: ComboBoxModel<String>) {
        invokeLater {
            modelComboBox.apply {
                model = placeholderModel
                isEnabled = false
            }
        }
    }
}
