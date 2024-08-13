package ee.carlrobert.codegpt.settings.documentation

import com.intellij.openapi.components.*

@Service
@State(
    name = "CodeGPT_DocumentationSettings",
    storages = [Storage("CodeGPT_DocumentationSettings.xml")]
)
class DocumentationSettings :
    SimplePersistentStateComponent<DocumentationSettingsState>(DocumentationSettingsState())

class DocumentationSettingsState : BaseState() {
    var documentations by list<DocumentationDetailsState>()
}

class DocumentationDetailsState : BaseState() {
    var name by string("CodeGPT Docs")
    var url by string("https://docs.codegpt.ee")
}