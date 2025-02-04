package ee.carlrobert.codegpt.settings.service.custom.form

import com.intellij.icons.AllIcons.General
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.service.custom.*
import ee.carlrobert.codegpt.settings.service.custom.form.model.mapToData
import ee.carlrobert.codegpt.settings.service.custom.template.CustomServiceTemplate
import ee.carlrobert.codegpt.ui.UIUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toImmutableList
import java.awt.Dimension
import java.awt.FlowLayout
import java.net.MalformedURLException
import java.net.URL
import javax.swing.Box
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.ListSelectionModel

class CustomServiceListForm(
    state: CustomServicesState,
    private val coroutineScope: CoroutineScope
) {

    private val formState = MutableStateFlow(state.mapToData())

    private var lastSelectedIndex = 0

    private val customProvidersJBList = JBList(formState.value.services)
        .apply {
            setCellRenderer(CustomServiceNameListRenderer())
            selectionMode = ListSelectionModel.SINGLE_SELECTION

            addListSelectionListener { _ ->
                val localSelectedIndex = selectedIndex
                if (localSelectedIndex != -1) {
                    if (lastSelectedIndex != -1) {
                        updateStateFromForm(lastSelectedIndex)
                    }

                    lastSelectedIndex = localSelectedIndex
                    updateFormData(lastSelectedIndex)
                }
            }
        }

    init {
        formState
            .onEach {
                customProvidersJBList.setListData(it.services.toTypedArray())
                customProvidersJBList.repaint()
            }
            .launchIn(coroutineScope)
    }

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
    }
    private val nameField = JBTextField().apply {
        columns = 30
    }
    private val templateHelpText = JBLabel(General.ContextHelp)
    private val templateComboBox = ComboBox(EnumComboBoxModel(CustomServiceTemplate::class.java))
    private val chatCompletionsForm: CustomServiceChatCompletionForm
    private val codeCompletionsForm: CustomServiceCodeCompletionForm
    private val tabbedPane: JTabbedPane

    init {
        apiKeyField.text = runBlocking(Dispatchers.IO) {
            getCredential(CredentialKey.CUSTOM_SERVICE_API_KEY)
        }

        val selectedItem = formState.value.services.first()
        chatCompletionsForm = CustomServiceChatCompletionForm(selectedItem.chatCompletionSettings, this::getApiKey)
        codeCompletionsForm = CustomServiceCodeCompletionForm(selectedItem.codeCompletionSettings, this::getApiKey)
        tabbedPane = JTabbedPane().apply {
            add(CodeGPTBundle.get("shared.chatCompletions"), chatCompletionsForm.form)
            add(CodeGPTBundle.get("shared.codeCompletions"), codeCompletionsForm.form)
            templateComboBox.selectedItem = selectedItem.template
        }
        nameField.text = selectedItem.name
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
                    parseResponseAsChatCompletions = template.codeCompletionTemplate.parseResponseAsChatCompletions
                }
                tabbedPane.setEnabledAt(1, true)
            } else {
                tabbedPane.selectedIndex = 0
                tabbedPane.setEnabledAt(1, false)
            }
        }
        updateTemplateHelpTextTooltip(selectedItem.template)
    }

    private fun updateFormData(index: Int) {
        val selectedItem = formState.value.services[index]

        chatCompletionsForm.apply {
            val chatCompletionSettings = selectedItem.chatCompletionSettings
            url = chatCompletionSettings.url.orEmpty()
            body = chatCompletionSettings.body.toMutableMap()
            headers = chatCompletionSettings.headers.toMutableMap()
        }
        codeCompletionsForm.apply {
            val codeCompletionSettings = selectedItem.codeCompletionSettings
            url = codeCompletionSettings.url.orEmpty()
            body = codeCompletionSettings.body.toMutableMap()
            headers = codeCompletionSettings.headers.toMutableMap()
            infillTemplate = codeCompletionSettings.infillTemplate
            codeCompletionsEnabled = codeCompletionSettings.codeCompletionsEnabled
            parseResponseAsChatCompletions = codeCompletionSettings.parseResponseAsChatCompletions
        }
        nameField.text = selectedItem.name
        updateTemplateHelpTextTooltip(selectedItem.template)
    }

    private fun updateStateFromForm(editedIndex: Int) {
        formState.update { state ->
            val editedItem = state.services[editedIndex]

            val updatedItem = editedItem.copy(
                name = nameField.text,
                template = templateComboBox.item,
                chatCompletionSettings = editedItem.chatCompletionSettings.copy(
                    url = chatCompletionsForm.url,
                    body = chatCompletionsForm.body,
                    headers = chatCompletionsForm.headers,
                ),
                codeCompletionSettings = editedItem.codeCompletionSettings.copy(
                    codeCompletionsEnabled = codeCompletionsForm.codeCompletionsEnabled,
                    parseResponseAsChatCompletions = codeCompletionsForm.parseResponseAsChatCompletions,
                    infillTemplate = codeCompletionsForm.infillTemplate,
                    url = codeCompletionsForm.url,
                    headers = codeCompletionsForm.headers,
                    body = codeCompletionsForm.body,
                )
            )

            if (editedItem == updatedItem) return@update state

            val updatedServices = state.services.toMutableList().let { mutableList ->
                mutableList[editedIndex] = updatedItem
                mutableList.toImmutableList()
            }
            state.copy(services = updatedServices)
        }
    }

    fun getForm(): JPanel =
        BorderLayoutPanel(8, 0)
            .addToLeft(createToolbarDecorator().createPanel())
            .addToCenter(createContentPanel())

    private fun createToolbarDecorator(): ToolbarDecorator =
        ToolbarDecorator.createDecorator(customProvidersJBList)
            .setPreferredSize(Dimension(220, 0))
            .setAddAction { handleAddAction() }
            .setRemoveAction { handleRemoveAction() }
            .setRemoveActionUpdater {
                formState.value.services.size > 1
            }
            .disableUpDownActions()

    private fun handleRemoveAction() {
        val prevSelectedIndex = customProvidersJBList.selectedIndex
        formState.update { state ->
            state.copy(services = state.services.filterIndexed { index, _ ->
                index != customProvidersJBList.selectedIndex
            })
        }
        val newSelectedIndex = if (prevSelectedIndex == 0) {
            0
        } else {
            prevSelectedIndex - 1
        }
        lastSelectedIndex = -1
        updateFormData(newSelectedIndex)
        customProvidersJBList.selectedIndex = newSelectedIndex
    }

    private fun handleAddAction() {
        formState.update {
            it.copy(
                services = it.services + CustomServiceSettingsState().mapToData()
            )
        }
        customProvidersJBList.selectedIndex = formState.value.services.lastIndex
    }

    private fun createContentPanel(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.presetTemplate.label"),
            JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
                add(templateComboBox)
                add(Box.createHorizontalStrut(8))
                add(templateHelpText)
            }
        )
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.apiKey.provider.name"),
            nameField
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

    fun isModified() =
        service<CustomServicesSettings>().state.mapToData() !=
                updateStateFromForm(lastSelectedIndex).let { formState.value }

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
                parseResponseAsChatCompletions = codeCompletionsForm.parseResponseAsChatCompletions
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