package ee.carlrobert.codegpt.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import org.jetbrains.annotations.NotNull;

public class AskAction extends AnAction {

  public AskAction() {
    super("Ask ChatGPT", "Ask ChatGPT description", AllIcons.Actions.Find);
    ActionsUtil.registerOrReplaceAction(this);
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
      var tabPanel = project.getService(ChatContentManagerService.class).createNewTabPanel();
      if (tabPanel != null) {
        tabPanel.displayLandingView();
      }
    }
  }
}
