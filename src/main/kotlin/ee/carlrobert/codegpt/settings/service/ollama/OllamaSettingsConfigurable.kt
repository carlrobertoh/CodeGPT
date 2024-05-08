package ee.carlrobert.codegpt.settings.service.ollama


import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class OllamaSettingsConfigurable : Configurable {

    private lateinit var component: OllamaSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Ollama Service"
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
    }

    override fun reset() {
        component.resetForm()
    }
}