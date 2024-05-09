package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm
import javax.swing.JComponent

class AzureServiceConfigurable : Configurable {

    private lateinit var component: AzureSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Azure Service"
    }

    override fun createComponent(): JComponent {
        component = AzureSettingsForm(service<AzureSettings>().state)
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<AzureSettings>().state
                || component.getActiveDirectoryToken() != getCredential(AZURE_ACTIVE_DIRECTORY_TOKEN)
                || component.getApiKey() != getCredential(AZURE_OPENAI_API_KEY)
    }

    override fun apply() {
        service<GeneralSettings>().state.selectedService = ServiceType.AZURE
        service<AzureSettings>().loadState(component.currentState)
        setCredential(AZURE_OPENAI_API_KEY, component.getApiKey())
        setCredential(AZURE_ACTIVE_DIRECTORY_TOKEN, component.getActiveDirectoryToken())
    }

    override fun reset() {
        component.resetForm()
    }
}