package ee.carlrobert.codegpt.settings.service.google;

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.GOOGLE_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import javax.swing.JComponent

class GoogleSettingsConfigurable : Configurable {

    private lateinit var component: GoogleSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Google Service"
    }

    override fun createComponent(): JComponent {
        component = GoogleSettingsForm()
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.isModified() || component.getApiKey() != getCredential(GOOGLE_API_KEY)
    }

    override fun apply() {
        setCredential(GOOGLE_API_KEY, component.getApiKey())
        service<GeneralSettings>().state.selectedService = ServiceType.GOOGLE
        component.applyChanges()
    }

    override fun reset() {
        component.resetForm()
    }
}