package ee.carlrobert.codegpt.toolwindow.chat

import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.util.EditorDiffUtil

class CompareWithOriginalActionLink(
    project: Project,
    toolwindowEditor: Editor,
    highlightedText: String,
) : ToolwindowEditorActionLink(
    project,
    CodeGPTBundle.get("action.compareWithOriginal.title"),
    CompareWithOriginalAction(project, toolwindowEditor, highlightedText),
    highlightedText
) {

    init {
        setIcon(Actions.Diff)
    }

    class CompareWithOriginalAction(
        private val project: Project,
        private val toolwindowEditor: Editor,
        private val highlightedText: String
    ) : AnAction() {

        override fun actionPerformed(e: AnActionEvent) {
            EditorDiffUtil.showDiff(project, toolwindowEditor, highlightedText)
        }
    }
}
