package ee.carlrobert.codegpt.ui.textarea.suggestion.item

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.documentation.DocumentationSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.ui.DocumentationDetails
import ee.carlrobert.codegpt.util.ResourceUtil.getDefaultPersonas
import ee.carlrobert.codegpt.util.file.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeParseException

class FileSuggestionGroupItem(private val project: Project) : SuggestionGroupItem {
    override val displayName: String = CodeGPTBundle.get("suggestionGroupItem.files.displayName")
    override val icon = AllIcons.FileTypes.Any_type

    override suspend fun getSuggestions(searchText: String?): List<SuggestionActionItem> {
        if (searchText == null) {
            val projectFileIndex = project.service<ProjectFileIndex>()
            return readAction {
                project.service<FileEditorManager>().openFiles
                    .filter { projectFileIndex.isInContent(it) }
                    .toFileSuggestions()
            }
        }
        return FileUtil.searchProjectFiles(project, searchText).toFileSuggestions()
    }

    private fun Iterable<VirtualFile>.toFileSuggestions() = take(10).map { FileActionItem(it) }
}

class FolderSuggestionGroupItem(private val project: Project) : SuggestionGroupItem {
    private val projectFoldersCache = mutableMapOf<Project, List<VirtualFile>>()

    override val displayName: String = CodeGPTBundle.get("suggestionGroupItem.folders.displayName")
    override val icon = AllIcons.Nodes.Folder

    override suspend fun getSuggestions(searchText: String?): List<SuggestionActionItem> {
        if (searchText == null) {
            return getProjectFolders(project).toFolderSuggestions()
        }
        return getProjectFolders(project)
            .filter { it.path.contains(searchText, ignoreCase = true) }
            .toFolderSuggestions()
    }

    private fun Iterable<VirtualFile>.toFolderSuggestions() = take(10).map { FolderActionItem(it) }

    private suspend fun getProjectFolders(project: Project) =
        projectFoldersCache.getOrPut(project) { findProjectFolders(project) }

    private suspend fun findProjectFolders(project: Project) = withContext(Dispatchers.IO) {
        val folders = mutableSetOf<VirtualFile>()
        project.service<ProjectFileIndex>().iterateContent { file: VirtualFile ->
            if (file.isDirectory && !file.name.startsWith(".")) {
                val folderPath = file.path
                if (folders.none { it.path.startsWith(folderPath) }) {
                    folders.removeAll { it.path.startsWith(folderPath) }
                    folders.add(file)
                }
            }
            true
        }
        folders.toList()
    }
}

class PersonaSuggestionGroupItem : SuggestionGroupItem {
    override val displayName: String = CodeGPTBundle.get("suggestionGroupItem.personas.displayName")
    override val icon = AllIcons.General.User

    override suspend fun getSuggestions(searchText: String?): List<SuggestionActionItem> =
        getDefaultPersonas()
            .filter {
                if (searchText.isNullOrEmpty()) {
                    true
                } else {
                    it.name.contains(searchText, true)
                }
            }
            .map { PersonaActionItem(it) }
            .take(10) + listOf(CreatePersonaActionItem())
}

class DocumentationSuggestionGroupItem : SuggestionGroupItem {
    override val displayName: String = CodeGPTBundle.get("suggestionGroupItem.docs.displayName")
    override val icon = AllIcons.Toolwindows.Documentation
    override val enabled = GeneralSettings.getSelectedService() == ServiceType.CODEGPT

    override suspend fun getSuggestions(searchText: String?): List<SuggestionActionItem> =
        service<DocumentationSettings>().state.documentations
            .sortedByDescending { parseDateTime(it.lastUsedDateTime) }
            .filter {
                if (searchText.isNullOrEmpty()) {
                    true
                } else {
                    it.name?.contains(searchText, true) ?: false
                }
            }
            .take(10)
            .map {
                DocumentationActionItem(DocumentationDetails(it.name ?: "", it.url ?: ""))
            } + listOf(CreateDocumentationActionItem(), ViewAllDocumentationsActionItem())

    private fun parseDateTime(dateTimeString: String?): Instant {
        return dateTimeString?.let {
            try {
                Instant.parse(it)
            } catch (e: DateTimeParseException) {
                Instant.EPOCH
            }
        } ?: Instant.EPOCH
    }
}