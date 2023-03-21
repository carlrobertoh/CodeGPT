package ee.carlrobert.codegpt.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.ContentManagerService;
import ee.carlrobert.codegpt.toolwindow.ToolWindowService;
import org.jetbrains.annotations.NotNull;

public class AskAction extends AnAction {

  public AskAction() {
    super("Ask ChatGPT", "Ask ChatGPT description", AllIcons.Actions.Find);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(event.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      ConversationsState.getInstance().startConversation();
      project.getService(ContentManagerService.class).displayChatTab(project);
      var chatToolWindow = project.getService(ToolWindowService.class).getChatToolWindow();
      chatToolWindow.displayLandingView();
    }
  }
}
