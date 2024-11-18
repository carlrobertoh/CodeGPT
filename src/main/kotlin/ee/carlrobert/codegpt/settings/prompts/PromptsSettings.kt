package ee.carlrobert.codegpt.settings.prompts

import com.intellij.openapi.components.*

@Service
@State(
    name = "CodeGPT_PromptsSettings",
    storages = [Storage("CodeGPT_PromptsSettings.xml")]
)
class PromptsSettings :
    SimplePersistentStateComponent<PromptsSettingsState>(PromptsSettingsState()) {

}

class PromptsSettingsState : BaseState() {

}