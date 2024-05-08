package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.ANTHROPIC_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettingsForm
import javax.swing.JComponent

class AnthropicServiceConfigurable : Configurable {

    private lateinit var component: AnthropicSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Anthropic Service"
    }

    override fun createComponent(): JComponent {
        component = AnthropicSettingsForm(service<AnthropicSettings>().state)
        return component.form
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<AnthropicSettings>().state
                || component.getApiKey() != getCredential(ANTHROPIC_API_KEY)
    }

    override fun apply() {
        setCredential(ANTHROPIC_API_KEY, component.getApiKey())
        service<GeneralSettings>().state.selectedService = ServiceType.ANTHROPIC
        service<AnthropicSettings>().loadState(component.getCurrentState())
    }

    override fun reset() {
        component.resetForm()
    }
}