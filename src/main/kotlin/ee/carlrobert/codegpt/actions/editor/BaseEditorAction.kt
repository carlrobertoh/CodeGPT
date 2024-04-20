package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsActions
import javax.swing.Icon

abstract class BaseEditorAction @JvmOverloads constructor(
  text: @NlsActions.ActionText String?,
  description: @NlsActions.ActionDescription String?,
  icon: Icon? = null
) : AnAction(text, description, icon) {
  init {
    EditorActionsUtil.registerAction(this)
  }

  protected abstract fun actionPerformed(project: Project, editor: Editor, selectedText: String?)

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    val editor = event.getData(CommonDataKeys.EDITOR) ?: return
    actionPerformed(project, editor, editor.selectionModel.selectedText)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = event.project != null
            && event.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText != null
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.EDT
  }
}
