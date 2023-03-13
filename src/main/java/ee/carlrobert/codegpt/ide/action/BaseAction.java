package ee.carlrobert.codegpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.toolwindow.ContentManagerService;
import ee.carlrobert.codegpt.ide.toolwindow.ToolWindowService;
import org.jetbrains.annotations.NotNull;

public abstract class BaseAction extends AnAction {

  protected abstract void actionPerformed(Project project, Editor editor, String selectedText);

  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null && project != null) {
      actionPerformed(project, editor, editor.getSelectionModel().getSelectedText());
    }
  }

  public void update(AnActionEvent event) {
    Project project = event.getProject();
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      menuAllowed = editor.getSelectionModel().getSelectedText() != null;
    }
    event.getPresentation().setEnabled(menuAllowed);
  }

  protected void sendMessage(@NotNull Project project, String prompt) {
    ConversationsState.getInstance().startConversation();
    ContentManagerService.getInstance(project).displayChatTab();

    var toolWindowService = project.getService(ToolWindowService.class);
    var chatToolWindow = toolWindowService.getChatToolWindow();
    chatToolWindow.show();
    chatToolWindow.clearWindow();
    chatToolWindow.displayUserMessage(prompt);
    chatToolWindow.sendMessage(prompt, project);
  }
}
