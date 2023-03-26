package ee.carlrobert.codegpt.toolwindow.conversations.actions;

import static icons.Icons.DefaultImageIcon;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import org.jetbrains.annotations.NotNull;

public class ClearAllConversationsAction extends AnAction {

  private final Runnable onRefresh;

  public ClearAllConversationsAction(Runnable onRefresh) {
    super("Delete All", "Delete all conversations", AllIcons.Actions.GC);
    this.onRefresh = onRefresh;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(!ConversationsState.getInstance().getSortedConversations().isEmpty());
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    int answer = Messages.showYesNoDialog("Are you sure you want to delete all conversations?", "Clear History", DefaultImageIcon);
    if (answer == Messages.YES) {
      ConversationsState.getInstance().clearAll();
      var project = event.getProject();
      if (project != null) {
        project.getService(ChatContentManagerService.class).resetTabbedPane();
      }
      this.onRefresh.run();
    }
  }
}
