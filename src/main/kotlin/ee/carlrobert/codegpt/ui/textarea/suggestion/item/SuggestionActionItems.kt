package ee.carlrobert.codegpt.ui.textarea.suggestion.item

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.documentation.DocumentationSettings
import ee.carlrobert.codegpt.settings.documentation.DocumentationsConfigurable
import ee.carlrobert.codegpt.settings.persona.PersonaDetails
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.settings.persona.PersonasConfigurable
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.ui.AddDocumentationDialog
import ee.carlrobert.codegpt.ui.DocumentationDetails
import ee.carlrobert.codegpt.ui.textarea.CustomTextPane
import ee.carlrobert.codegpt.ui.textarea.FileSearchService

class FileActionItem(val file: VirtualFile) : SuggestionActionItem {
    override val displayName = file.name
    override val icon = file.fileType.icon ?: AllIcons.FileTypes.Any_type

    override fun execute(project: Project, textPane: CustomTextPane) {
        project.getService(FileSearchService::class.java).addFileToSession(file)
        textPane.appendHighlightedText(file.name, ':', replacement = false)
    }
}

class FolderActionItem(val folder: VirtualFile) : SuggestionActionItem {
    override val displayName = folder.name
    override val icon = AllIcons.Nodes.Folder

    override fun execute(project: Project, textPane: CustomTextPane) {
        val fileSearchService = project.service<FileSearchService>()
        folder.children
            .filter { !it.isDirectory }
            .forEach { fileSearchService.addFileToSession(it) }
        textPane.appendHighlightedText(folder.path, ':', replacement = false)
    }
}

class PersonaActionItem(val personaDetails: PersonaDetails) : SuggestionActionItem {
    override val displayName = personaDetails.name
    override val icon = AllIcons.General.User

    override fun execute(project: Project, textPane: CustomTextPane) {
        service<PersonaSettings>().state.selectedPersona.apply {
            id = personaDetails.id
            name = personaDetails.name
            instructions = personaDetails.instructions
        }
        textPane.appendHighlightedText(personaDetails.name, ':')
    }
}

class DocumentationActionItem(
    val documentationDetails: DocumentationDetails
) : SuggestionActionItem {
    override val displayName = documentationDetails.name
    override val icon = AllIcons.Toolwindows.Documentation
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, textPane: CustomTextPane) {
        CodeGPTKeys.ADDED_DOCUMENTATION.set(project, documentationDetails)
        service<DocumentationSettings>().updateLastUsedDateTime(documentationDetails.url)
        textPane.appendHighlightedText(documentationDetails.name, ':')
    }
}

class CreateDocumentationActionItem : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.createDocumentation.displayName")
    override val icon = AllIcons.General.Add
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, textPane: CustomTextPane) {
        val addDocumentationDialog = AddDocumentationDialog(project)
        if (addDocumentationDialog.showAndGet()) {
            service<DocumentationSettings>()
                .updateLastUsedDateTime(addDocumentationDialog.documentationDetails.url)
            textPane.appendHighlightedText(
                addDocumentationDialog.documentationDetails.name,
                searchChar = ':',
            )
        }
    }
}

class ViewAllDocumentationsActionItem : SuggestionActionItem {
    override val displayName: String =
        "${CodeGPTBundle.get("suggestionActionItem.viewDocumentations.displayName")} →"
    override val icon = null
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, textPane: CustomTextPane) {
        service<ShowSettingsUtil>().showSettingsDialog(
            project,
            DocumentationsConfigurable::class.java
        )
    }
}

class CreatePersonaActionItem : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.createPersona.displayName")
    override val icon = AllIcons.General.Add

    override fun execute(project: Project, textPane: CustomTextPane) {
        service<ShowSettingsUtil>().showSettingsDialog(
            project,
            PersonasConfigurable::class.java
        )
    }
}

class WebSearchActionItem(private val onWebSearchIncluded: () -> Unit) : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.webSearch.displayName")
    override val icon = AllIcons.General.Web
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, textPane: CustomTextPane) {
        onWebSearchIncluded()
        textPane.appendHighlightedText("web")
    }
}