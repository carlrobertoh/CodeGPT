package ee.carlrobert.codegpt.settings.prompts.form.details

import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import ee.carlrobert.codegpt.settings.prompts.ChatActionPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.CodeAssistantPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.CoreActionPromptDetailsState
import ee.carlrobert.codegpt.settings.prompts.PersonaPromptDetailsState
import javax.swing.JComponent

interface PromptDetailsPanel {
    fun getPanel(): JComponent
    fun updateData(details: FormPromptDetails)
    fun remove(details: FormPromptDetails)
}

sealed class FormPromptDetails {
    abstract var name: String?
    abstract var instructions: String?
}

data class CodeAssistantPromptDetails(
    override var name: String?,
    override var instructions: String?,
    val code: String?
) : FormPromptDetails() {
    constructor(state: CodeAssistantPromptDetailsState) : this(
        name = state.name,
        instructions = state.instructions,
        code = state.code
    )
}

data class CoreActionPromptDetails(
    override var name: String?,
    override var instructions: String?,
    val code: String?
) : FormPromptDetails() {
    constructor(state: CoreActionPromptDetailsState) : this(
        name = state.name,
        instructions = state.instructions,
        code = state.code
    )
}

data class ChatActionPromptDetails(
    override var name: String?,
    override var instructions: String?,
    val id: Long,
    val code: String?,
    var requiresAdditionalInput: Boolean = false
) : FormPromptDetails() {
    constructor(state: ChatActionPromptDetailsState) : this(
        name = state.name,
        instructions = state.instructions,
        id = state.id,
        code = state.code,
    )
}

data class PersonaPromptDetails(
    override var name: String?,
    override var instructions: String?,
    val id: Long,
    var selected: AtomicBooleanProperty = AtomicBooleanProperty(false),
    var disabled: Boolean = false
) : FormPromptDetails() {
    constructor(state: PersonaPromptDetailsState) : this(
        name = state.name,
        instructions = state.instructions,
        id = state.id,
        disabled = state.disabled
    )
}