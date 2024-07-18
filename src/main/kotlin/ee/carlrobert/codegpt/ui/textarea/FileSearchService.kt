package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier
import ee.carlrobert.codegpt.util.file.FileUtil
import kotlinx.coroutines.*
import java.io.File

@Service
class FileSearchService private constructor(val project: Project) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun searchFiles(searchText: String): List<String> = runBlocking {
        withContext(scope.coroutineContext) {
            FileUtil.searchProjectFiles(project, searchText).map { it.path }
        }
    }

    fun addFileToSession(file: VirtualFile) {
        val filesIncluded =
            project.getUserData(CodeGPTKeys.SELECTED_FILES).orEmpty().toMutableList()
        filesIncluded.add(ReferencedFile(File(file.path)))
        updateFilesInSession(filesIncluded)
    }

    fun removeFilesFromSession() = updateFilesInSession(mutableListOf())

    private fun updateFilesInSession(files: MutableList<ReferencedFile>) {
        project.putUserData(CodeGPTKeys.SELECTED_FILES, files)
        project.messageBus
            .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
            .filesIncluded(files)
    }
}
