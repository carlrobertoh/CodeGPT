package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.util.ResourceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path

interface SuggestionStrategy {
    suspend fun getSuggestions(project: Project, searchText: String? = null): List<SuggestionItem>
}

class FileSuggestionStrategy : SuggestionStrategy {
    override suspend fun getSuggestions(
        project: Project,
        searchText: String?
    ): List<SuggestionItem> {
        if (searchText == null) {
            val projectFileIndex = project.service<ProjectFileIndex>()
            return readAction {
                project.service<FileEditorManager>().openFiles
                    .filter { projectFileIndex.isInContent(it) }
                    .take(10)
                    .map { SuggestionItem.FileItem(File(it.path)) }
            }
        }
        return project.service<FileSearchService>().searchFiles(searchText)
            .take(10)
            .map { SuggestionItem.FileItem(File(it)) }
    }
}

class FolderSuggestionStrategy : SuggestionStrategy {
    private val projectFoldersCache = mutableMapOf<Project, List<String>>()

    override suspend fun getSuggestions(
        project: Project,
        searchText: String?
    ): List<SuggestionItem> {
        if (searchText == null) {
            return getProjectFolders(project)
                .take(10)
                .map { SuggestionItem.FolderItem(Path.of(it).toFile()) }
        }
        return getProjectFolders(project)
            .filter { it.contains(searchText, ignoreCase = true) }
            .take(10)
            .map { SuggestionItem.FolderItem(Path.of(it).toFile()) }
    }

    private suspend fun getProjectFolders(project: Project): List<String> {
        return projectFoldersCache.getOrPut(project) {
            findProjectFolders(project)
        }
    }

    private suspend fun findProjectFolders(project: Project): List<String> =
        withContext(Dispatchers.IO) {
            val uniqueFolders = mutableSetOf<String>()
            val iterator = ContentIterator { file: VirtualFile ->
                if (file.isDirectory && !file.name.startsWith(".")) {
                    val folderPath = file.path
                    if (uniqueFolders.none { it.startsWith(folderPath) }) {
                        uniqueFolders.removeAll { it.startsWith(folderPath) }
                        uniqueFolders.add(folderPath)
                    }
                }
                true
            }

            project.service<ProjectFileIndex>().iterateContent(iterator)
            uniqueFolders.toList()
        }
}

class PersonaSuggestionStrategy : SuggestionStrategy {
    override suspend fun getSuggestions(
        project: Project,
        searchText: String?
    ): List<SuggestionItem> {
        if (searchText == null) {
            return ResourceUtil.getFilteredPersonaSuggestions(null)
        }
        return ResourceUtil.getFilteredPersonaSuggestions {
            it.name.contains(searchText, true)
        }
    }
}

class DefaultSuggestionStrategy : SuggestionStrategy {
    override suspend fun getSuggestions(
        project: Project,
        searchText: String?
    ): List<SuggestionItem> = emptyList()
}