package ee.carlrobert.codegpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.toolwindow.ContentManagerService;
import ee.carlrobert.codegpt.ide.toolwindow.ToolWindowService;
import org.jetbrains.annotations.NotNull;

public class AskAction extends AnAction {

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(event.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      ConversationsState.getInstance().startConversation();
      ContentManagerService.getInstance(project).displayChatTab();
      var chatToolWindow = project.getService(ToolWindowService.class).getChatToolWindow();
      chatToolWindow.show();
      chatToolWindow.displayLandingView();
    }
  }
}
