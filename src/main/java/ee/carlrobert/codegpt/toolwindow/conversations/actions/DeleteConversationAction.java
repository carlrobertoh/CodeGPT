package ee.carlrobert.codegpt.toolwindow.conversations.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import org.jetbrains.annotations.NotNull;

public class DeleteConversationAction extends AnAction {

  private final Runnable onRefresh;

  public DeleteConversationAction(Runnable onRefresh) {
    super("Delete Conversation", "Delete single conversation", AllIcons.General.Remove);
    this.onRefresh = onRefresh;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    int answer = Messages.showYesNoDialog("Are you sure you want to delete this convesation?", "Delete Converation", null);
    if (answer == Messages.YES) {
      ConversationsState.getInstance().deleteSelectedConversation();
      onRefresh.run();
    }
  }
}
