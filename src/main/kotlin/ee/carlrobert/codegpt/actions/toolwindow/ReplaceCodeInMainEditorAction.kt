package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.util.EditorUtil.hasSelection
import ee.carlrobert.codegpt.util.EditorUtil.isMainEditorTextSelected
import ee.carlrobert.codegpt.util.EditorUtil.replaceMainEditorSelection
import java.util.Objects.requireNonNull

class ReplaceCodeInMainEditorAction :
  AnAction("Replace in Main Editor", "Replace code in main editor", AllIcons.Actions.Replace) {
  init {
    registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = isMainEditorTextSelected(requireNonNull(event.project)!!)
            && hasSelection(event.getData(PlatformDataKeys.EDITOR))
  }

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    val toolWindowEditor = event.getData(PlatformDataKeys.EDITOR) ?: return
    replaceMainEditorSelection(project, requireNonNull(toolWindowEditor.selectionModel.selectedText)!!)
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.EDT
  }
}
