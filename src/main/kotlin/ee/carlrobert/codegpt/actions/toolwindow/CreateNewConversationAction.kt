package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.telemetry.TelemetryAction

class CreateNewConversationAction(private val onCreate: Runnable) :
  AnAction("Create New Chat", "Create new chat", AllIcons.General.Add) {

  init {
    registerAction(this)
  }

  override fun actionPerformed(event: AnActionEvent) {
    try {
      event.project ?: return
      onCreate.run()
    } finally {
      TelemetryAction.IDE_ACTION.createActionMessage()
        .property("action", ActionType.CREATE_NEW_CHAT.name)
        .send()
    }
  }
}
