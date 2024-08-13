package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier
import java.io.File

@Service
class FileSearchService private constructor(val project: Project) {

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
