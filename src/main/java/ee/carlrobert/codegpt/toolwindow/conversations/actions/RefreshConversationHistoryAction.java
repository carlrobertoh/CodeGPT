package ee.carlrobert.codegpt.toolwindow.conversations.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefreshConversationHistoryAction extends AnAction {

  private final Runnable onRefresh;

  public RefreshConversationHistoryAction(Runnable onRefresh) {
    super("Refresh History", "Reload conversations list", AllIcons.Actions.Refresh);
    this.onRefresh = onRefresh;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
      this.onRefresh.run();
  }
}
