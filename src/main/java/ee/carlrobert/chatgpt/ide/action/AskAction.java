package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindowManager;
import ee.carlrobert.chatgpt.client.ApiClient;
import ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowService;
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
      var toolWindow = ToolWindowManager.getInstance(project).getToolWindow("ChatGPT");
      if (toolWindow != null) {
        toolWindow.show();
        toolWindow.setTitle("");
        var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
        ApiClient.getInstance().clearQueries();
        toolWindowService.removeAll();
        toolWindowService.paintLandingView();
      }
    }
  }
}
