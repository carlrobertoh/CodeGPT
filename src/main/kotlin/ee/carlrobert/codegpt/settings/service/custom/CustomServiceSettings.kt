package ee.carlrobert.codegpt.settings.service.custom

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.OptionTag
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate
import ee.carlrobert.codegpt.settings.service.custom.template.CustomServiceChatCompletionTemplate
import ee.carlrobert.codegpt.settings.service.custom.template.CustomServiceCodeCompletionTemplate
import ee.carlrobert.codegpt.settings.service.custom.template.CustomServiceTemplate
import ee.carlrobert.codegpt.util.MapConverter

@Service
@State(
    name = "CodeGPT_CustomServiceSettings",
    storages = [Storage("CodeGPT_CustomServiceSettings.xml")]
)
class CustomServiceSettings :
    SimplePersistentStateComponent<CustomServiceState>(CustomServiceState()) {

    override fun loadState(state: CustomServiceState) {
        if (state.url != null || state.body.isNotEmpty() || state.headers.isNotEmpty()) {
            super.loadState(this.state.apply {
                // Migrate old settings
                template = state.template
                chatCompletionSettings.url = state.url
                chatCompletionSettings.body = state.body
                chatCompletionSettings.headers = state.headers
                url = null
                body = mutableMapOf()
                headers = mutableMapOf()
            })
        } else {
            super.loadState(state)
        }
    }
}

class CustomServiceState : BaseState() {
    var template by enum(CustomServiceTemplate.OPENAI)
    var chatCompletionSettings by property(CustomServiceChatCompletionSettingsState())
    var codeCompletionSettings by property(CustomServiceCodeCompletionSettingsState())

    @Deprecated("", ReplaceWith("this.chatCompletionSettings.url"))
    var url by string()

    @Deprecated("", ReplaceWith("this.chatCompletionSettings.headers"))
    var headers by map<String, String>()

    @get:OptionTag(converter = MapConverter::class)
    @Deprecated("", ReplaceWith("this.chatCompletionSettings.body"))
    var body by map<String, Any>()
}

class CustomServiceChatCompletionSettingsState : BaseState() {
    var url by string(CustomServiceChatCompletionTemplate.OPENAI.url)
    var headers by map<String, String>()

    @get:OptionTag(converter = MapConverter::class)
    var body by map<String, Any>()

    init {
        headers.putAll(CustomServiceChatCompletionTemplate.OPENAI.headers)
        body.putAll(CustomServiceChatCompletionTemplate.OPENAI.body)
    }
}

class CustomServiceCodeCompletionSettingsState : BaseState() {
    var codeCompletionsEnabled by property(true)
    var infillTemplate by enum(InfillPromptTemplate.OPENAI)
    var url by string(CustomServiceCodeCompletionTemplate.OPENAI.url)
    var headers by map<String, String>()

    @get:OptionTag(converter = MapConverter::class)
    var body by map<String, Any>()

    init {
        headers.putAll(CustomServiceCodeCompletionTemplate.OPENAI.headers)
        body.putAll(CustomServiceCodeCompletionTemplate.OPENAI.body)
    }
}
