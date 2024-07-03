package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm
import javax.swing.JComponent

class OpenAIServiceConfigurable : Configurable {

    private lateinit var component: OpenAISettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: OpenAI Service"
    }

    override fun createComponent(): JComponent {
        component = OpenAISettingsForm(service<OpenAISettings>().state)
        return component.form
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<OpenAISettings>().state
                || component.getApiKey() != getCredential(OPENAI_API_KEY)
    }

    override fun apply() {
        service<GeneralSettings>().state.selectedService = ServiceType.OPENAI
        setCredential(OPENAI_API_KEY, component.getApiKey())
        service<OpenAISettings>().loadState(component.getCurrentState())
    }

    override fun reset() {
        component.resetForm()
    }
}