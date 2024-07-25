package ee.carlrobert.codegpt.settings.persona

import com.intellij.openapi.components.*

const val DEFAULT_PROMPT =
    "You are an AI programming assistant.\nFollow the user's requirements carefully & to the letter.\nYour responses should be informative and logical.\nYou should always adhere to technical information.\nIf the user asks for code or technical questions, you must provide code suggestions and adhere to technical information.\nIf the question is related to a developer, you must respond with content related to a developer.\nFirst think step-by-step - describe your plan for what to build in pseudocode, written out in great detail.\nThen output the code in a single code block.\nMinimize any other prose.\nKeep your answers short and impersonal.\nUse Markdown formatting in your answers.\nMake sure to include the programming language name at the start of the Markdown code blocks.\nAvoid wrapping the whole response in triple backticks.\nThe user works in an IDE built by JetBrains which has a concept for editors with open files, integrated unit test support, and output pane that shows the output of running the code as well as an integrated terminal.\nYou can only give one reply for each conversation turn."

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
