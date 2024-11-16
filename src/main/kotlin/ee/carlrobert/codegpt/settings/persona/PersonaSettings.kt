package ee.carlrobert.codegpt.settings.persona

import com.intellij.openapi.components.*
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent

val DEFAULT_PROMPT = getResourceContent("/prompts/default-completion.txt")

@Service
@State(
    name = "CodeGPT_PersonaSettings",
    storages = [Storage("CodeGPT_PersonaSettings.xml")]
)
class PersonaSettings :
    SimplePersistentStateComponent<PersonaSettingsState>(PersonaSettingsState()) {

    companion object {
        @JvmStatic
        fun getSystemPrompt(): String {
            return service<PersonaSettings>().state.selectedPersona.instructions ?: ""
        }
    }
}

class PersonaSettingsState : BaseState() {
    var selectedPersona by property(PersonaDetailsState())
    var userCreatedPersonas by list<PersonaDetailsState>()
}

class PersonaDetailsState : BaseState() {
    var id by property(1L)
    var name by string("CodeGPT Default")
    var instructions by string(DEFAULT_PROMPT)
}

@JvmRecord
data class PersonaDetails(val id: Long, val name: String, val instructions: String)

fun PersonaDetails.toPersonaDetailsState(): PersonaDetailsState {
    val newState = PersonaDetailsState()
    newState.id = id
    newState.name = name
    newState.instructions = instructions
    return newState
}
