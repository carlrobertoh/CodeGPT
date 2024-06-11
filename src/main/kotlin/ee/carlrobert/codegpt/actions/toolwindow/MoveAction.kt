package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.conversations.Conversation
import ee.carlrobert.codegpt.conversations.ConversationsState
import java.util.Optional
import javax.swing.Icon

abstract class MoveAction protected constructor(
  text: String?, description: String?, icon: Icon?, private val onRefresh: Runnable) : AnAction(text, description, icon) {

  protected abstract fun getConversation(project: Project): Optional<Conversation?>

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = ConversationsState.getCurrentConversation() != null
  }

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    getConversation(project).ifPresent {
      ConversationsState.getInstance().setCurrentConversation(it)
      onRefresh.run()
    }
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }
}
