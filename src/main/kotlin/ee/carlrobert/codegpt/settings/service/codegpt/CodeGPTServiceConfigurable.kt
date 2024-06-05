package ee.carlrobert.codegpt.settings.service.codegpt

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CODEGPT_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.util.ApplicationUtil
import javax.swing.JComponent

class CodeGPTServiceConfigurable : Configurable {

    private lateinit var component: CodeGPTServiceForm

    override fun getDisplayName(): String {
        return "CodeGPT: CodeGPT Service"
    }

    override fun createComponent(): JComponent {
        component = CodeGPTServiceForm()
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.isModified() || component.getApiKey() != getCredential(CODEGPT_API_KEY)
    }

    override fun apply() {
        setCredential(CODEGPT_API_KEY, component.getApiKey())
        service<GeneralSettings>().state.apply {
            selectedService = ServiceType.CODEGPT
        }
        ApplicationUtil.findCurrentProject()
            ?.service<CodeGPTService>()
            ?.syncUserDetailsAsync(component.getApiKey())
        component.applyChanges()
    }

    override fun reset() {
        component.resetForm()
    }
}