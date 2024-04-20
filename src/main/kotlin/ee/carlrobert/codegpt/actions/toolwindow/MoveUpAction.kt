package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.conversations.Conversation
import ee.carlrobert.codegpt.conversations.ConversationService
import java.util.Optional

class MoveUpAction(onRefresh: Runnable?) : MoveAction("Move Up", "Move up", AllIcons.Actions.MoveUp, onRefresh!!) {
  init {
    registerAction(this)
  }

  override fun getConversation(project: Project): Optional<Conversation?> {
    return ConversationService.getInstance().nextConversation
  }
}
