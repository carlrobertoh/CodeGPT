package ee.carlrobert.codegpt.settings.service.google

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.GOOGLE_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.llm.client.google.models.GoogleModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.swing.JPanel

class GoogleSettingsForm {

    private val apiKeyField = JBPasswordField()
    private val completionModelComboBox: ComboBox<GoogleModel>

    init {
        val state = service<GoogleSettings>().state
        apiKeyField.columns = 30
        apiKeyField.text = runBlocking(Dispatchers.IO) {
            getCredential(GOOGLE_API_KEY)
        }
        completionModelComboBox = ComboBox(
            EnumComboBoxModel(GoogleModel::class.java)
        )
        completionModelComboBox.selectedItem = GoogleModel.findByCode(state.model)
    }


    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.google.apiKey.comment")
        )
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.shared.model.label"),
            completionModelComboBox
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.google.model.comment")
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
        apiKeyField.text = getCredential(GOOGLE_API_KEY)
        completionModelComboBox.selectedItem = GoogleModel.findByCode(state.model)
    }

    fun isModified(): Boolean = service<GoogleSettings>().state.run {
        model != getModel() || getApiKey() != getCredential(GOOGLE_API_KEY)
    }

    fun applyChanges() {
        service<GoogleSettings>().state.run {
            model = getModel()
        }
    }
}
