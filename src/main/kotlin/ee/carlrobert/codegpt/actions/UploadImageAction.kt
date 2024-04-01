package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.UploadImageNotifier

class UploadImageAction : AnAction("Upload Image", "Upload an image", Icons.Upload) {

    override fun actionPerformed(e: AnActionEvent) {
        FileChooser.chooseFiles(createSingleImageFileDescriptor(), e.project, null).also { files ->
            if (files.isNotEmpty()) {
                check(files.size == 1) { "Expected exactly one file to be selected" }
                e.project?.let { project ->
                    CodeGPTKeys.UPLOADED_FILE_PATH[project] = files.first().path
                    project.messageBus
                        .syncPublisher(UploadImageNotifier.UPLOADED_FILE_PATH_TOPIC)
                        .fileUploaded(files.first().path)
                }
            }
        }
    }

    private fun createSingleImageFileDescriptor() = FileChooserDescriptor(
        true, false, false, false, false, false
    ).apply {
        withFileFilter { file ->
            file.extension in listOf("jpg", "jpeg", "png")
        }
        withTitle("Select Image")
    }
}