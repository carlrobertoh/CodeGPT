package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.conversations.ConversationsState.getCurrentConversation
import ee.carlrobert.codegpt.telemetry.TelemetryAction

class ClearChatWindowAction(private val onActionPerformed: Runnable) :
  AnAction("Clear Window", "Clears a chat window", AllIcons.General.Reset) {
  init {
    registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    event.presentation.isEnabled = getCurrentConversation()?.messages?.isNotEmpty() == true
  }

  override fun actionPerformed(event: AnActionEvent) {
    try {
      onActionPerformed.run()
    } finally {
      TelemetryAction.IDE_ACTION.createActionMessage()
        .property("action", ActionType.CLEAR_CHAT_WINDOW.name)
        .send()
    }
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }
}
