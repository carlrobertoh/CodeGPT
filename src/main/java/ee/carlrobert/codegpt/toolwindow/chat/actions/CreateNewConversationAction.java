package ee.carlrobert.codegpt.toolwindow.chat.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import org.jetbrains.annotations.NotNull;

public class CreateNewConversationAction extends AnAction {

  private final Runnable onCreate;

  public CreateNewConversationAction(Runnable onCreate) {
    super("Create New Chat", "Create new chat", AllIcons.General.Add);
    this.onCreate = onCreate;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      ConversationsState.getInstance().startConversation();
      onCreate.run();
    }
  }
}
