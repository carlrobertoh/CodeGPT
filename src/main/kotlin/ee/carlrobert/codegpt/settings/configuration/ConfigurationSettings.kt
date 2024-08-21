package ee.carlrobert.codegpt.settings.configuration

import com.intellij.openapi.components.*
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil
import ee.carlrobert.codegpt.completions.CompletionRequestProvider.GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT
import kotlin.math.max
import kotlin.math.min

@Service
@State(
    name = "CodeGPT_ConfigurationSettings_210",
    storages = [Storage("CodeGPT_ConfigurationSettings_210.xml")]
)
class ConfigurationSettings :
    SimplePersistentStateComponent<ConfigurationSettingsState>(ConfigurationSettingsState()) {
    companion object {
        @JvmStatic
        fun getState(): ConfigurationSettingsState {
            return service<ConfigurationSettings>().state
        }
    }
}

class ConfigurationSettingsState : BaseState() {
    var commitMessagePrompt by string(GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT)
    var maxTokens by property(2048)
    var temperature by property(0.1f) { max(0f, min(1f, it)) }
    var checkForPluginUpdates by property(true)
    var checkForNewScreenshots by property(false)
    var createNewChatOnEachAction by property(false)
    var ignoreGitCommitTokenLimit by property(false)
    var methodNameGenerationEnabled by property(true)
    var captureCompileErrors by property(true)
    var autoFormattingEnabled by property(true)
    var autocompletionPostProcessingEnabled by property(false)
    var autocompletionContextAwareEnabled by property(false)
    var tableData by map<String, String>()

    init {
        tableData.putAll(EditorActionsUtil.DEFAULT_ACTIONS)
    }
}