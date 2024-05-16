package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier

class AttachImageAction : AnAction(
    CodeGPTBundle.get("action.attachImage"),
    CodeGPTBundle.get("action.attachImageDescription"),
    Icons.Upload
) {

    override fun actionPerformed(e: AnActionEvent) {
        FileChooser.chooseFiles(createSingleImageFileDescriptor(), e.project, null).also { files ->
            if (files.isNotEmpty()) {
                check(files.size == 1) { "Expected exactly one file to be selected" }
                e.project?.let { addImageAttachment(it, files.first().path) }
            }
        }
    }

    private fun createSingleImageFileDescriptor() = FileChooserDescriptor(
        true, false, false, false, false, false
    ).apply {
        withFileFilter { file ->
            file.extension in SUPPORTED_EXTENSIONS
        }
        withTitle(CodeGPTBundle.get("imageFileChooser.title"))
    }

    companion object {
        @JvmField
        var SUPPORTED_EXTENSIONS = listOf("jpg", "jpeg", "png")

        @JvmStatic
        fun addImageAttachment(project: Project, filePath: String) {
            CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH[project] = filePath
            project.messageBus
                .syncPublisher(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC)
                .imageAttached(filePath)
        }
    }
}