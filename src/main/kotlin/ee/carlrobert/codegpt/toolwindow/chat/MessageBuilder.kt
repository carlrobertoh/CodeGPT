package ee.carlrobert.codegpt.toolwindow.chat

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.ui.textarea.TagProcessorFactory
import ee.carlrobert.codegpt.ui.textarea.header.HeaderTagDetails
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditor

class MessageBuilder(private val project: Project, private val text: String) {
    private val message = Message("")
    private var editorContent: String = ""
    private var inlayContent: String = ""

    fun withSelectedEditorContent(): MessageBuilder {
        getSelectedEditor(project)?.let { editor ->
            editorContent = processEditorSelectedText(editor)
        }
        return this
    }

    fun withInlays(appliedTags: List<HeaderTagDetails>): MessageBuilder {
        if (appliedTags.isNotEmpty()) {
            inlayContent = processTags(message, appliedTags)
        }
        return this
    }

    fun withReferencedFiles(referencedFiles: List<ReferencedFile>): MessageBuilder {
        if (referencedFiles.isNotEmpty()) {
            message.referencedFilePaths = referencedFiles.map { it.filePath() }
        }
        return this
    }

    fun withImage(attachedImagePath: String): MessageBuilder {
        message.imageFilePath = attachedImagePath
        return this
    }

    fun build(): Message {
        message.prompt = buildString {
            append(text)
            if (editorContent.isNotBlank()) {
                append("\n\n")
                append(editorContent)
            }
            if (inlayContent.isNotBlank()) {
                append("\n")
                append(inlayContent)
            }
        }.trim()
        return message
    }

    private fun processEditorSelectedText(editor: Editor): String {
        return editor.selectionModel.selectedText?.let { selectedText ->
            if (selectedText.isBlank()) return ""

            val fileExtension = (editor as EditorEx).virtualFile?.name?.substringAfterLast('.', "") ?: ""
            editor.selectionModel.removeSelection()

            "```$fileExtension\n$selectedText\n```"
        } ?: ""
    }

    private fun processTags(
        message: Message,
        tags: List<HeaderTagDetails>
    ): String = buildString {
        tags.forEach {
            TagProcessorFactory.getProcessor(project, it).process(message, it, this)
        }
    }
}
