package ee.carlrobert.codegpt.toolwindow.conversations.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
    ConversationsState.getInstance().clearAll();
    this.onRefresh.run();
  }
}
