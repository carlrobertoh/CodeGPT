package ee.carlrobert.codegpt.settings.service.codegpt

import com.intellij.openapi.components.*

@Service
@State(
    name = "CodeGPT_CodeGPTServiceSettings_280",
    storages = [Storage("CodeGPT_CodeGPTServiceSettings_280.xml")]
)
class CodeGPTServiceSettings :
    SimplePersistentStateComponent<CodeGPTServiceSettingsState>(CodeGPTServiceSettingsState())

class CodeGPTServiceSettingsState : BaseState() {
    var chatCompletionSettings by property(CodeGPTServiceChatCompletionSettingsState())
    var codeCompletionSettings by property(CodeGPTServiceCodeCompletionSettingsState())
}

class CodeGPTServiceChatCompletionSettingsState : BaseState() {
    var model by string("llama-3-8b")
}

class CodeGPTServiceCodeCompletionSettingsState : BaseState() {
    var codeCompletionsEnabled by property(false)
    var model by string("starcoder-7b")
}
