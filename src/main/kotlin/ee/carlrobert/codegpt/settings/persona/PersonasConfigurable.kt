package ee.carlrobert.codegpt.settings.persona

import com.intellij.openapi.components.service
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

    override fun isModified(): Boolean {
        service<PersonaSettings>().state.let {
            val (id, name, description) = component.getSelectedPersona() ?: return false
            return it.selectedPersona.id != id
                    || it.selectedPersona.name != name
                    || it.selectedPersona.description != description
                    || component.removedItemIds.size > 0
                    || component.addedItems.size > 0
        }
    }

    override fun apply() {
        val persona = component.getSelectedPersona()
        service<PersonaSettings>().state.run {
            if (persona != null) {
                selectedPersona = persona.toPersonaDetailsState()
            }

            userCreatedPersonas.removeIf { component.removedItemIds.contains(it.id) }
            userCreatedPersonas.addAll(component.addedItems.map { it.toPersonaDetailsState() })
        }
        component.clear()
    }

    override fun reset() {
        component.resetChanges()
    }
}