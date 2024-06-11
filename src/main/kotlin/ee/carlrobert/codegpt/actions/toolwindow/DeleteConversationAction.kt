package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.telemetry.TelemetryAction
import ee.carlrobert.codegpt.ui.OverlayUtil

class DeleteConversationAction(private val onDelete: Runnable) :
  AnAction("Delete Conversation", "Delete single conversation", AllIcons.Actions.GC) {
  init {
    registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = ConversationsState.getCurrentConversation() != null
  }

  override fun actionPerformed(event: AnActionEvent) {
    if (OverlayUtil.showDeleteConversationDialog() == Messages.YES && event.project != null) {
      TelemetryAction.IDE_ACTION.createActionMessage()
        .property("action", ActionType.DELETE_CONVERSATION.name)
        .send()
      onDelete.run()
    }
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.EDT
  }
}
