package ee.carlrobert.codegpt.settings.service.codegpt

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.panel.ComponentPanelBuilder
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.fields.IntegerField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CODEGPT_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.llm.client.codegpt.CodeGPTAvailableModels
import ee.carlrobert.llm.client.codegpt.CodeGPTAvailableModels.AVAILABLE_CHAT_MODELS
import ee.carlrobert.llm.client.codegpt.CodeGPTAvailableModels.AVAILABLE_CODE_MODELS
import ee.carlrobert.llm.client.codegpt.CodeGPTModel
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.JPanel

class CodeGPTServiceForm {

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
        text = getCredential(CredentialKey.CUSTOM_SERVICE_API_KEY)
    }

    private val chatCompletionModelComboBox =
        ComboBox(ListComboBoxModel(AVAILABLE_CHAT_MODELS)).apply {
            selectedItem =
                CodeGPTAvailableModels.findByCode(service<CodeGPTServiceSettings>().state.chatCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        service<CodeGPTServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
    )

    private val codeCompletionModelComboBox =
        ComboBox(ListComboBoxModel(AVAILABLE_CODE_MODELS)).apply {
            selectedItem =
                CodeGPTAvailableModels.findByCode(service<CodeGPTServiceSettings>().state.codeCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    private val codeCompletionMaxTokensField =
        IntegerField("completion_max_tokens", 8, 4096).apply {
            columns = 12
            value = service<CodeGPTServiceSettings>().state.codeCompletionSettings.maxTokens
        }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator(CodeGPTBundle.get("shared.configuration")))
        .addComponent(
            FormBuilder.createFormBuilder()
                .setFormLeftIndent(16)
                .addLabeledComponent(
                    CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"),
                    apiKeyField
                )
                .addComponentToRightColumn(
                    UIUtil.createComment("settingsConfigurable.service.codegpt.apiKey.comment")
                )
                .addLabeledComponent("Model:", chatCompletionModelComboBox)
                .addVerticalGap(4)
                .panel
        )
        .addComponent(TitledSeparator("Code Completions"))
        .addComponent(
            FormBuilder.createFormBuilder()
                .setFormLeftIndent(16)
                .addComponent(codeCompletionsEnabledCheckBox)
                .addLabeledComponent("Model:", codeCompletionModelComboBox)
                .addLabeledComponent("Max tokens:", codeCompletionMaxTokensField)
                .addComponentToRightColumn(
                    ComponentPanelBuilder.createCommentComponent(
                        CodeGPTBundle.get("codeCompletionsForm.maxTokensComment"), true, 48, true
                    )
                )
                .panel
        )
        .panel

    fun getApiKey() = String(apiKeyField.password).ifEmpty { null }

    fun isModified() = service<CodeGPTServiceSettings>().state.run {
        (chatCompletionModelComboBox.selectedItem as CodeGPTModel).code != chatCompletionSettings.model
                || (codeCompletionModelComboBox.selectedItem as CodeGPTModel).code != codeCompletionSettings.model
                || codeCompletionMaxTokensField.value != codeCompletionSettings.maxTokens
                || codeCompletionsEnabledCheckBox.isSelected != codeCompletionSettings.codeCompletionsEnabled
                || getApiKey() != getCredential(CODEGPT_API_KEY)
    }

    fun applyChanges() {
        service<CodeGPTServiceSettings>().state.run {
            chatCompletionSettings.model =
                (chatCompletionModelComboBox.selectedItem as CodeGPTModel).code
            codeCompletionSettings.codeCompletionsEnabled =
                codeCompletionsEnabledCheckBox.isSelected
            codeCompletionSettings.maxTokens = codeCompletionMaxTokensField.value
            codeCompletionSettings.model =
                (codeCompletionModelComboBox.selectedItem as CodeGPTModel).code
        }
        setCredential(CODEGPT_API_KEY, getApiKey())
    }

    fun resetForm() {
        service<CodeGPTServiceSettings>().state.run {
            chatCompletionModelComboBox.selectedItem = chatCompletionSettings.model
            codeCompletionModelComboBox.selectedItem = codeCompletionSettings.model
            codeCompletionMaxTokensField.value = codeCompletionSettings.maxTokens
            codeCompletionsEnabledCheckBox.isSelected =
                codeCompletionSettings.codeCompletionsEnabled
        }
        apiKeyField.text = getCredential(CODEGPT_API_KEY)
    }

    private class CustomComboBoxRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val component =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is CodeGPTModel) {
                text = value.name
            }
            return component
        }
    }
}