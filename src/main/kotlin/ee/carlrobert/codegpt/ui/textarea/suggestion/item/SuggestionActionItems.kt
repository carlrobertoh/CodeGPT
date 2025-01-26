package ee.carlrobert.codegpt.ui.textarea.suggestion.item

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.documentation.DocumentationSettings
import ee.carlrobert.codegpt.settings.documentation.DocumentationsConfigurable
import ee.carlrobert.codegpt.settings.prompts.PersonaDetails
import ee.carlrobert.codegpt.settings.prompts.PromptsConfigurable
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.ui.AddDocumentationDialog
import ee.carlrobert.codegpt.ui.DocumentationDetails
import ee.carlrobert.codegpt.ui.textarea.UserInputPanel
import ee.carlrobert.codegpt.ui.textarea.header.tag.*
import git4idea.GitCommit
import javax.swing.Icon

class FileActionItem(val file: VirtualFile) : SuggestionActionItem {
    override val displayName = file.name
    override val icon = file.fileType.icon ?: AllIcons.FileTypes.Any_type

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(FileTagDetails(file))
    }
}

class IncludeOpenFilesActionItem : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.includeOpenFiles.displayName")
    override val icon: Icon = Icons.ListFiles

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        val fileTags = userInputPanel.getSelectedTags().filterIsInstance<FileTagDetails>()
        project.service<FileEditorManager>().openFiles
            .filter { openFile ->
                fileTags.none { it.virtualFile == openFile }
            }
            .forEach {
                userInputPanel.addTag(FileTagDetails(it))
            }
    }
}

class FolderActionItem(val folder: VirtualFile) : SuggestionActionItem {
    override val displayName = folder.name
    override val icon = AllIcons.Nodes.Folder

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(FolderTagDetails(folder))
    }
}

class PersonaActionItem(val personaDetails: PersonaDetails) : SuggestionActionItem {
    override val displayName = personaDetails.name
    override val icon = AllIcons.General.User

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(PersonaTagDetails(personaDetails))
    }
}

class DocumentationActionItem(
    val documentationDetails: DocumentationDetails
) : SuggestionActionItem {
    override val displayName = documentationDetails.name
    override val icon = AllIcons.Toolwindows.Documentation
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        service<DocumentationSettings>().updateLastUsedDateTime(documentationDetails.url)
        userInputPanel.addTag(DocumentationTagDetails(documentationDetails))
    }
}

class CreateDocumentationActionItem : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.createDocumentation.displayName")
    override val icon = AllIcons.General.Add
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        val addDocumentationDialog = AddDocumentationDialog(project)
        if (addDocumentationDialog.showAndGet()) {
            service<DocumentationSettings>()
                .updateLastUsedDateTime(addDocumentationDialog.documentationDetails.url)
            userInputPanel.addTag(DocumentationTagDetails(addDocumentationDialog.documentationDetails))
        }
    }
}

class GitCommitActionItem(
    val gitCommit: GitCommit,
) : SuggestionActionItem {

    val description: String = gitCommit.id.asString().take(6)

    override val displayName: String = gitCommit.subject
    override val icon = AllIcons.Vcs.CommitNode

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(GitCommitTagDetails(gitCommit))
    }
}

class IncludeCurrentGitChangesActionItem : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.includeCurrentChanges.displayName")
    override val icon: Icon? = null

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(CurrentGitChangesTagDetails())
    }
}

class ViewAllDocumentationsActionItem : SuggestionActionItem {
    override val displayName: String =
        "${CodeGPTBundle.get("suggestionActionItem.viewDocumentations.displayName")} →"
    override val icon = null
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
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

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        service<ShowSettingsUtil>().showSettingsDialog(
            project,
            PromptsConfigurable::class.java
        )
    }
}

class WebSearchActionItem(private val project: Project) : SuggestionActionItem {
    override val displayName: String =
        CodeGPTBundle.get("suggestionActionItem.webSearch.displayName")
    override val icon = AllIcons.General.Web
    override val enabled: Boolean
        get() = enabled()

    fun enabled(): Boolean {
        if (GeneralSettings.getSelectedService() != ServiceType.CODEGPT) {
            return false
        }
        return !TagUtil.isTagTypePresent(project, WebTagDetails::class.java)
    }

    override fun execute(project: Project, userInputPanel: UserInputPanel) {
        userInputPanel.addTag(WebTagDetails())

    }
}