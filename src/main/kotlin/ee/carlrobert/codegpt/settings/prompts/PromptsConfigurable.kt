package ee.carlrobert.codegpt.settings.prompts

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PromptsConfigurable : Configurable {

    private lateinit var component: PromptsForm

    override fun getDisplayName(): String {
        return "CodeGPT: Prompts"
    }

    override fun createComponent(): JComponent {
        component = PromptsForm()
        return component.createPanel()
    }

    override fun isModified(): Boolean = false

    override fun apply() {

    }

    override fun reset() {

    }
}