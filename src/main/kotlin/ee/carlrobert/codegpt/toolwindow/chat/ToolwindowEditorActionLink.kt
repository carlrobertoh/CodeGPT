package ee.carlrobert.codegpt.toolwindow.chat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.AnActionLink

open class ToolwindowEditorActionLink(
    private val project: Project,
    title: String,
    action: AnAction,
    private val highlightedText: String,
) : AnActionLink(title, action) {

    private val mainEditor = project.service<FileEditorManager>().selectedTextEditor
    private val documentListener = object : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            updateActionState()
        }
    }

    init {
        mainEditor?.document?.addDocumentListener(documentListener)
        autoHideOnDisable = false
    }

    private fun updateActionState() {
        val mainEditor = project.service<FileEditorManager>().selectedTextEditor
        val startIndex = mainEditor?.document?.text?.indexOf(highlightedText)
        runInEdt {
            isEnabled = startIndex != null && startIndex != -1
            toolTipText = if (isEnabled) null else "Original state has changed"
        }
    }
}
