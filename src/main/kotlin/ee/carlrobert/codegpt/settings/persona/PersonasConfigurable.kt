package ee.carlrobert.codegpt.settings.persona

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PersonasConfigurable : Configurable {

    private lateinit var component: PersonasSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Personas"
    }

    override fun createComponent(): JComponent {
        component = PersonasSettingsForm()
        return component.createPanel()
    }

    override fun isModified(): Boolean = component.isModified()

    override fun apply() {
        component.applyChanges()
    }

    override fun reset() {
        component.resetChanges()
    }
}