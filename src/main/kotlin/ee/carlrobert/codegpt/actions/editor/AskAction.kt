package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager

class AskAction : AnAction("New Chat", "Chat with CodeGPT", Icons.Sparkle) {
  init {
    EditorActionsUtil.registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = event.project != null
  }

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    ConversationsState.getInstance().setCurrentConversation(null)
    project.getService(ChatToolWindowContentManager::class.java)
      .createNewTabPanel()?.displayLandingView()
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }
}
