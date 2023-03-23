package ee.carlrobert.codegpt.toolwindow.conversations.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import org.jetbrains.annotations.NotNull;

public class ClearAllConversationsAction extends AnAction {

  private final Runnable onRefresh;

  public ClearAllConversationsAction(Runnable onRefresh) {
    super("Delete All", "Delete all conversations", AllIcons.Actions.GC);
    this.onRefresh = onRefresh;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    int answer = Messages.showYesNoDialog("Are you sure you want to delete all convesation?", "Clear History", null);
    if (answer == Messages.YES) {
      ConversationsState.getInstance().clearAll();
      this.onRefresh.run();
    }
  }
}
