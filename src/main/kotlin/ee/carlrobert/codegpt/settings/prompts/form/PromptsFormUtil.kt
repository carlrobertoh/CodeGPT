package ee.carlrobert.codegpt.settings.prompts.form

import ee.carlrobert.codegpt.settings.prompts.ChatActionPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.CodeAssistantPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.CoreActionPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.PersonaPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.form.details.*
import javax.swing.tree.DefaultMutableTreeNode

object PromptsFormUtil {

    fun CodeAssistantPromptDetails.toState(): CodeAssistantPromptDetailsState {
        val state = CodeAssistantPromptDetailsState()
        state.code = this.code
        state.name = this.name
        state.instructions = this.instructions
        return state
    }

    fun CoreActionPromptDetails.toState(): CoreActionPromptDetailsState {
        val state = CoreActionPromptDetailsState()
        state.code = this.code
        state.name = this.name
        state.instructions = this.instructions
        return state
    }

    fun ChatActionPromptDetails.toState(): ChatActionPromptDetailsState {
        val state = ChatActionPromptDetailsState()
        state.id = this.id
        state.code = this.code
        state.name = this.name
        state.instructions = this.instructions
        return state
    }

    fun PersonaPromptDetails.toState(): PersonaPromptDetailsState {
        val state = PersonaPromptDetailsState()
        state.id = this.id
        state.name = this.name
        state.instructions = this.instructions
        state.disabled = this.disabled
        return state
    }

    inline fun <reified T : FormPromptDetails> getFormState(
        formNode: DefaultMutableTreeNode,
    ): List<T> {
        return formNode.children().toList()
            .filterIsInstance<PromptDetailsTreeNode>()
            .map { it.details }
            .filterIsInstance<T>()
    }
}