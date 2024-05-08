package ee.carlrobert.codegpt.settings.service.google

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.llm.client.google.models.GoogleModel
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent

class GoogleSettingsForm {

    private val apiKeyField = JBPasswordField()
    private val completionModelComboBox: ComboBox<GoogleModel>

    init {
        val state = service<GoogleSettings>().state
        apiKeyField.columns = 30
        apiKeyField.text =
            getCredential(CredentialKey.GOOGLE_API_KEY)
        completionModelComboBox = ComboBox(
            EnumComboBoxModel(GoogleModel::class.java)
        )
        completionModelComboBox.selectedItem = GoogleModel.findByCode(state.model)
    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator(CodeGPTBundle.get("shared.configuration")))
        .addComponent(
            UIUtil.withEmptyLeftBorder(
                UI.PanelFactory.grid()
                    .add(
                        UI.PanelFactory.panel(apiKeyField)
                            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
                            .resizeX(false)
                            .withComment(CodeGPTBundle.get("settingsConfigurable.service.google.apiKey.comment"))
                            .withCommentHyperlinkListener { event: HyperlinkEvent? ->
                                UIUtil.handleHyperlinkClicked(
                                    event
                                )
                            })

                    .add(
                        UI.PanelFactory.panel(completionModelComboBox)
                            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.model.label"))
                            .resizeX(false)
                            .withComment(CodeGPTBundle.get("settingsConfigurable.service.google.model.comment"))
                            .withCommentHyperlinkListener { event: HyperlinkEvent? ->
                                UIUtil.handleHyperlinkClicked(
                                    event
                                )
                            }
                    )
                    .createPanel()
            )
        )
        .addComponentFillVertically(JPanel(), 0)
        .panel


    fun getApiKey(): String? = String(apiKeyField.password).ifEmpty { null }

    fun getModel(): String = (completionModelComboBox.model
        .selectedItem as GoogleModel)
        .code

    fun getCurrentState() = GoogleSettingsState().apply { model = getModel() }

    fun resetForm() {
        val state = service<GoogleSettings>().state
        apiKeyField.text =
            getCredential(CredentialKey.GOOGLE_API_KEY)
        completionModelComboBox.selectedItem = GoogleModel.findByCode(state.model)
    }

    fun isModified(): Boolean = service<GoogleSettings>().state.run {
        model != getModel() || getApiKey() != getCredential(CredentialKey.GOOGLE_API_KEY)
    }

    fun applyChanges() {
        service<GoogleSettings>().state.run {
            model = getModel()
        }
    }

}
