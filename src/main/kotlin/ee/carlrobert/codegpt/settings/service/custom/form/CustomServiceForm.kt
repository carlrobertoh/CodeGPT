package ee.carlrobert.codegpt.settings.service.custom.form

import com.intellij.icons.AllIcons.General
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceChatCompletionSettingsState
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceCodeCompletionSettingsState
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.template.CustomServiceTemplate
import ee.carlrobert.codegpt.ui.UIUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.awt.FlowLayout
import java.net.MalformedURLException
import java.net.URL
import javax.swing.Box
import javax.swing.JPanel
import javax.swing.JTabbedPane

class CustomServiceForm {

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
    }
    private val templateHelpText = JBLabel(General.ContextHelp)
    private val templateComboBox = ComboBox(EnumComboBoxModel(CustomServiceTemplate::class.java))
    private val chatCompletionsForm: CustomServiceChatCompletionForm
    private val codeCompletionsForm: CustomServiceCodeCompletionForm
    private val tabbedPane: JTabbedPane

    init {
        val state = service<CustomServiceSettings>().state
        apiKeyField.text = runBlocking(Dispatchers.IO) {
            getCredential(CredentialKey.CUSTOM_SERVICE_API_KEY)
        }
        chatCompletionsForm =
            CustomServiceChatCompletionForm(state.chatCompletionSettings, this::getApiKey)
        codeCompletionsForm =
            CustomServiceCodeCompletionForm(state.codeCompletionSettings, this::getApiKey)
        tabbedPane = JTabbedPane().apply {
            add(CodeGPTBundle.get("shared.chatCompletions"), chatCompletionsForm.form)
            add(CodeGPTBundle.get("shared.codeCompletions"), codeCompletionsForm.form)
        }
        templateComboBox.selectedItem = state.template
        templateComboBox.addItemListener {
            val template = it.item as CustomServiceTemplate
            updateTemplateHelpTextTooltip(template)
            chatCompletionsForm.run {
                url = template.chatCompletionTemplate.url
                headers = template.chatCompletionTemplate.headers
                body = template.chatCompletionTemplate.body
            }
            if (template.codeCompletionTemplate != null) {
                codeCompletionsForm.run {
                    url = template.codeCompletionTemplate.url
                    headers = template.codeCompletionTemplate.headers
                    body = template.codeCompletionTemplate.body
                }
                tabbedPane.setEnabledAt(1, true)
            } else {
                tabbedPane.selectedIndex = 0
                tabbedPane.setEnabledAt(1, false)
            }
        }
        updateTemplateHelpTextTooltip(state.template)
    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.presetTemplate.label"),
            JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
                add(templateComboBox)
                add(Box.createHorizontalStrut(8))
                add(templateHelpText)
            }
        )
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.custom.openai.apiKey.comment")
        )
        .addVerticalGap(4)
        .addComponent(tabbedPane)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getApiKey() = String(apiKeyField.password).ifEmpty { null }

    fun isModified() = service<CustomServiceSettings>().state.run {
        templateComboBox.selectedItem != template
                || chatCompletionsForm.url != chatCompletionSettings.url
                || chatCompletionsForm.headers != chatCompletionSettings.headers
                || chatCompletionsForm.body != chatCompletionSettings.body
                || codeCompletionsForm.codeCompletionsEnabled != codeCompletionSettings.codeCompletionsEnabled
                || codeCompletionsForm.infillTemplate != codeCompletionSettings.infillTemplate
                || codeCompletionsForm.url != codeCompletionSettings.url
                || codeCompletionsForm.headers != codeCompletionSettings.headers
                || codeCompletionsForm.body != codeCompletionSettings.body
    }

    fun applyChanges() {
        service<CustomServiceSettings>().state.run {
            template = templateComboBox.item
            chatCompletionSettings = CustomServiceChatCompletionSettingsState().apply {
                url = chatCompletionsForm.url
                headers = chatCompletionsForm.headers
                body = chatCompletionsForm.body
            }
            codeCompletionSettings = CustomServiceCodeCompletionSettingsState().apply {
                codeCompletionsEnabled = codeCompletionsForm.codeCompletionsEnabled
                infillTemplate = codeCompletionsForm.infillTemplate
                url = codeCompletionsForm.url
                headers = codeCompletionsForm.headers
                body = codeCompletionsForm.body
            }
        }
    }

    fun resetForm() {
        service<CustomServiceSettings>().state.run {
            templateComboBox.item = template
            apiKeyField.text = getCredential(CredentialKey.CUSTOM_SERVICE_API_KEY)
            chatCompletionsForm.resetForm(chatCompletionSettings)
            codeCompletionsForm.resetForm(codeCompletionSettings)
        }
    }

    private fun updateTemplateHelpTextTooltip(template: CustomServiceTemplate) {
        templateHelpText.toolTipText = null
        try {
            HelpTooltip()
                .setTitle(template.providerName)
                .setBrowserLink(
                    CodeGPTBundle.get("settingsConfigurable.service.custom.openai.linkToDocs"),
                    URL(template.docsUrl)
                )
                .installOn(templateHelpText)
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
    }
}