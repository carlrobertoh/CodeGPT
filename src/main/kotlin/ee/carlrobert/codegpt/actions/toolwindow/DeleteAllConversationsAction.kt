package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.conversations.ConversationService
import ee.carlrobert.codegpt.telemetry.TelemetryAction
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager

class DeleteAllConversationsAction(private val onRefresh: Runnable) :
  AnAction("Delete All", "Delete all conversations", AllIcons.Actions.GC) {
  init {
    registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    event.project ?: return
    event.presentation.isEnabled = ConversationService.getInstance().sortedConversations.isNotEmpty()
  }

  override fun actionPerformed(event: AnActionEvent) {
    val answer = Messages.showYesNoDialog(
      "Are you sure you want to delete all conversations?",
      "Clear History",
      Icons.Default)
    if (answer == Messages.YES) {
      val project = event.project
      if (project != null) {
        try {
          ConversationService.getInstance().clearAll()
          project.getService(ChatToolWindowContentManager::class.java).resetAll()
        } finally {
          TelemetryAction.IDE_ACTION.createActionMessage()
            .property("action", ActionType.DELETE_ALL_CONVERSATIONS.name)
            .send()
        }
      }
      onRefresh.run()
    }
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }
}
