package ee.carlrobert.codegpt.settings.service.ollama

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CodeGPT_OllamaSettings_210", storages = [Storage("CodeGPT_OllamaSettings_210.xml")])
class OllamaSettings : PersistentStateComponent<OllamaSettingsState> {

    private var state = OllamaSettingsState()

    override fun getState(): OllamaSettingsState {
        return state
    }

    override fun loadState(state: OllamaSettingsState) {
        this.state = state
    }

    fun isModified(form: OllamaSettingsForm): Boolean {
        return form.getCurrentState() != state
    }

    fun setCodeCompletionsEnabled(enabled: Boolean) {
        state = state.copy(codeCompletionsEnabled = enabled)
    }

    fun setModel(model: String) {
        state = state.copy(model = model)
    }

    companion object {
        fun getCurrentState(): OllamaSettingsState {
            return getInstance().getState()
        }

        fun getInstance(): OllamaSettings {
            return ApplicationManager.getApplication().getService(
                OllamaSettings::class.java
            )
        }
    }
}
