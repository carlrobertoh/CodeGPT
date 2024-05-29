package ee.carlrobert.codegpt.settings.service.codegpt

import com.intellij.openapi.components.*

@Service
@State(
    name = "CodeGPT_CodeGPTServiceSettings",
    storages = [Storage("CodeGPT_CodeGPTServiceSettings.xml")]
)
class CodeGPTServiceSettings :
    SimplePersistentStateComponent<CodeGPTServiceSettingsState>(CodeGPTServiceSettingsState())

class CodeGPTServiceSettingsState : BaseState() {
    var chatCompletionSettings by property(CodeGPTServiceChatCompletionSettingsState())
    var codeCompletionSettings by property(CodeGPTServiceCodeCompletionSettingsState())
}

class CodeGPTServiceChatCompletionSettingsState : BaseState() {
    var model by string("meta-llama/Llama-3-70b-chat-hf")
}

class CodeGPTServiceCodeCompletionSettingsState : BaseState() {
    var codeCompletionsEnabled by property(false)
    var model by string("codellama/CodeLlama-70b-hf")
}
