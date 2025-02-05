package ee.carlrobert.codegpt.settings.service.custom

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CustomServiceApiKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.custom.form.CustomServiceListForm
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import javax.swing.JComponent

class CustomServiceConfigurable : Configurable {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Swing.immediate)
    private lateinit var component: CustomServiceListForm


    override fun getDisplayName(): String {
        return "CodeGPT: Custom Service"
    }

    override fun createComponent(): JComponent {
        component = CustomServiceListForm(service<CustomServicesSettings>().state, coroutineScope)
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.getApiKey() != getCredential(CustomServiceApiKey) || component.isModified()

    }

    override fun apply() {
        component.applyChanges()
        setCredential(CustomServiceApiKey, component.getApiKey())
        service<GeneralSettings>().state.selectedService = ServiceType.CUSTOM_OPENAI
    }

    override fun reset() {
        component.resetForm()
    }

    override fun disposeUIResources() {
        coroutineScope.cancel()
    }
}