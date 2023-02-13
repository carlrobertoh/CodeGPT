package ee.carlrobert.chatgpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import ee.carlrobert.chatgpt.client.ApiClient;
import ee.carlrobert.chatgpt.service.ToolWindowService;
import org.jetbrains.annotations.NotNull;

public abstract class BaseAction extends AnAction {

  protected abstract String getPrompt(String selectedText);

  protected abstract void initToolWindow(ToolWindow toolWindow);

  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null && project != null) {
      initToolWindow(ToolWindowManager.getInstance(project).getToolWindow("ChatGPT"));
      var selectedText = editor.getSelectionModel().getSelectedText();
      var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
      var scrollablePanel = toolWindowService.getScrollablePanel();
      ApiClient.getInstance().clearQueries();
      scrollablePanel.removeAll();
      toolWindowService.sendMessage(selectedText, getPrompt(selectedText));
    }
  }

  public void update(AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(e.getProject() != null);
  }
}

