package ee.carlrobert.codegpt.toolwindow.chat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTBundle

class DirectApplyActionLink(
    project: Project,
    toolwindowEditor: Editor,
    highlightedText: String,
) : ToolwindowEditorActionLink(
    project,
    CodeGPTBundle.get("action.applyDirectly.title"),
    DirectApplyAction(project, toolwindowEditor, highlightedText),
    highlightedText
) {

    class DirectApplyAction(
        private val project: Project,
        private val toolwindowEditor: Editor,
        private val highlightedText: String
    ) : AnAction() {

        override fun actionPerformed(e: AnActionEvent) {
            val mainEditor = FileEditorManager.getInstance(project).selectedTextEditor
                ?: throw IllegalStateException("No editor selected")
            val startIndex = mainEditor.document.text.indexOf(highlightedText)
            if (startIndex == -1) {
                return
            }

            val endIndex = startIndex + highlightedText.length
            val replacement = toolwindowEditor.document.text

            WriteCommandAction.runWriteCommandAction(project) {
                mainEditor.document.replaceString(startIndex, endIndex, replacement)
                mainEditor.caretModel.moveToOffset(startIndex + replacement.length)
                mainEditor.scrollingModel.scrollToCaret(ScrollType.CENTER)
            }
        }
    }
}
