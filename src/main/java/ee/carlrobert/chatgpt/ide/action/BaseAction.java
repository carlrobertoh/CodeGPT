package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import ee.carlrobert.chatgpt.client.ApiClient;
import ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowService;
import org.jetbrains.annotations.NotNull;

public abstract class BaseAction extends AnAction {

  protected abstract String getPrompt(String selectedText);

  protected abstract void initToolWindow(ToolWindow toolWindow);

  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null && project != null) {
      var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
      var selectedText = editor.getSelectionModel().getSelectedText();
      ApiClient.getInstance().clearQueries();
      initToolWindow(toolWindowService.getToolWindow());
      toolWindowService.removeAll();
      toolWindowService.paintUserMessage(selectedText);
      toolWindowService.sendMessage(getPrompt(selectedText), project, null);
    }
  }

  public void update(AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(e.getProject() != null);
  }
}

