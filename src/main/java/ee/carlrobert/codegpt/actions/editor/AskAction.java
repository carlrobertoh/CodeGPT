package ee.carlrobert.codegpt.actions.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import org.jetbrains.annotations.NotNull;

public class AskAction extends AnAction {

  public AskAction() {
    super("New Chat", "Chat with CodeGPT", Icons.SparkleIcon);
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(event.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      ConversationsState.getInstance().setCurrentConversation(null);
      var tabPanel =
          project.getService(StandardChatToolWindowContentManager.class).createNewTabPanel();
      if (tabPanel != null) {
        tabPanel.displayLandingView();
      }
    }
  }
}
