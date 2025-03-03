package ee.carlrobert.codegpt.settings.service.ollama


import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import javax.swing.JComponent

class OllamaSettingsConfigurable : Configurable {

    private lateinit var component: OllamaSettingsForm

    override fun getDisplayName(): String {
        return "ProxyAI: Ollama Service"
    }

    override fun createComponent(): JComponent {
        component = OllamaSettingsForm()
        return component.getForm()
    }

    override fun isModified(): Boolean {
        return component.isModified()
    }

    override fun apply() {
        component.applyChanges()
        service<GeneralSettings>().state.selectedService = ServiceType.OLLAMA
    }

    override fun reset() {
        component.resetForm()
    }
}