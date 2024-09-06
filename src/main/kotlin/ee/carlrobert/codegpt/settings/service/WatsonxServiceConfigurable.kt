package ee.carlrobert.codegpt.settings.service;
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.WATSONX_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm
import ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettings
import ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettingsForm
import javax.swing.JComponent

public class WatsonxServiceConfigurable: Configurable {

    private lateinit var component: WatsonxSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Watsonx Service"
    }

    override fun createComponent(): JComponent {
        component = WatsonxSettingsForm(service<WatsonxSettings>().state)
        return component.form
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<WatsonxSettings>().state
                || component.getApiKey() != getCredential(WATSONX_API_KEY)
    }

    override fun apply() {
        service<GeneralSettings>().state.selectedService = ServiceType.WATSONX
        setCredential(WATSONX_API_KEY, component.getApiKey())
        service<WatsonxSettings>().loadState(component.getCurrentState())
    }

    override fun reset() {
        component.resetForm()
    }
}



