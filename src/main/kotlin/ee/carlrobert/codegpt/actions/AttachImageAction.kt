package ee.carlrobert.codegpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier

class AttachImageAction : AnAction(
    CodeGPTBundle.get("action.attachImage"),
    CodeGPTBundle.get("action.attachImageDescription"),
    AllIcons.FileTypes.Image
) {

    override fun actionPerformed(e: AnActionEvent) {
        FileChooser.chooseFiles(createSingleImageFileDescriptor(), e.project, null).also { files ->
            if (files.isNotEmpty()) {
                check(files.size == 1) { "Expected exactly one file to be selected" }
                e.project?.let { project ->
                    CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH[project] = files.first().path
                    project.messageBus
                        .syncPublisher(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC)
                        .imageAttached(files.first().path)
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
        withTitle(CodeGPTBundle.get("imageFileChooser.title"))
    }
}