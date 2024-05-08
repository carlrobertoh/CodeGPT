package ee.carlrobert.codegpt.settings.service.custom

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.custom.form.CustomServiceForm
import javax.swing.JComponent

class CustomServiceConfigurable : Configurable {

    private lateinit var component: CustomServiceForm

    override fun getDisplayName(): String {
        return "CodeGPT: Custom Service"
    }

    override fun createComponent(): JComponent {
        component = CustomServiceForm()
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.isModified()
                || component.getApiKey() != getCredential(CUSTOM_SERVICE_API_KEY)
    }

    override fun apply() {
        setCredential(CUSTOM_SERVICE_API_KEY, component.getApiKey())
        service<GeneralSettings>().state.selectedService = ServiceType.CUSTOM_OPENAI
        component.applyChanges()
    }

    override fun reset() {
        component.resetForm()
    }
}