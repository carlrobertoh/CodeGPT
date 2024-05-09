package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.LLAMA_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm
import javax.swing.JComponent

class LlamaServiceConfigurable : Configurable {

    private lateinit var component: LlamaSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Custom Service"
    }

    override fun createComponent(): JComponent {
        component = LlamaSettingsForm(service<LlamaSettings>().state)
        return component
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<LlamaSettings>().state
                || component.llamaServerPreferencesForm.getApiKey() != getCredential(LLAMA_API_KEY)
    }

    override fun apply() {
        service<GeneralSettings>().state.selectedService = ServiceType.LLAMA_CPP
        setCredential(LLAMA_API_KEY, component.llamaServerPreferencesForm.getApiKey())
        service<LlamaSettings>().loadState(component.currentState)
    }

    override fun reset() {
        component.resetForm()
    }
}