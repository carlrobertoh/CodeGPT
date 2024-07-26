package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.util.ResourceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path
import javax.swing.DefaultListModel

interface SuggestionUpdateStrategy {
    fun populateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
    )

    fun updateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
        searchText: String,
    )
}

class FileSuggestionActionStrategy : SuggestionUpdateStrategy {
    override fun populateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
    ) {
        val projectFileIndex = project.service<ProjectFileIndex>()
        CoroutineScope(Dispatchers.Default).launch {
            val openFilePaths = project.service<FileEditorManager>().openFiles
                .filter { readAction { projectFileIndex.isInContent(it) } }
                .take(10)
                .map { file -> file.path }
            listModel.clear()
            listModel.addAll(openFilePaths.map { SuggestionItem.FileItem(File(it)) })
        }
    }

    override fun updateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
        searchText: String,
    ) {
        val filePaths = project.service<FileSearchService>().searchFiles(searchText).take(10)
        listModel.clear()
        listModel.addAll(filePaths.map { SuggestionItem.FileItem(File(it)) })
    }
}

class FolderSuggestionActionStrategy : SuggestionUpdateStrategy {
    private val projectFoldersCache = mutableMapOf<Project, List<String>>()

    override fun populateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val folderPaths = getProjectFolders(project)
                .take(10)
                .map { SuggestionItem.FolderItem(Path.of(it).toFile()) }
            listModel.clear()
            listModel.addAll(folderPaths)
        }
    }

    override fun updateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
        searchText: String
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val filteredFolders = getProjectFolders(project)
                .filter { it.contains(searchText, ignoreCase = true) }
                .take(10)
                .map { SuggestionItem.FolderItem(Path.of(it).toFile()) }
            listModel.clear()
            listModel.addAll(filteredFolders)
        }
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

class PersonaSuggestionActionStrategy : SuggestionUpdateStrategy {

    override fun populateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
    ) {
        listModel.clear()
        listModel.addAll(ResourceUtil.getFilteredPersonaSuggestions(null))
    }

    override fun updateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
        searchText: String,
    ) {
        listModel.clear()
        listModel.addAll(ResourceUtil.getFilteredPersonaSuggestions {
            it.name.contains(
                searchText,
                true
            )
        })
    }
}

class DefaultSuggestionActionStrategy : SuggestionUpdateStrategy {
    override fun populateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
    ) {
    }

    override fun updateSuggestions(
        project: Project,
        listModel: DefaultListModel<SuggestionItem>,
        searchText: String,
    ) {
    }
}