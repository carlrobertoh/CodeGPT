package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ee.carlrobert.chatgpt.ide.conversations.ConversationsState;
import ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowService;
import java.util.Arrays;
import java.util.Objects;
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

  protected void sendMessage(Project project, String prompt) {
    var toolWindowService = project.getService(ToolWindowService.class);
    var toolWindow = toolWindowService.getToolWindow(project);
    var contentManager = toolWindow.getContentManager();

    Arrays.stream(contentManager.getContents())
        .filter(it -> "Chat".equals(it.getTabName()))
        .findFirst()
        .ifPresentOrElse(
            contentManager::setSelectedContent,
            () -> contentManager.setSelectedContent(Objects.requireNonNull(contentManager.getContent(0)))
        );

    ConversationsState.getInstance().startConversation();

    toolWindowService.getToolWindow(project).show();
    toolWindowService.removeAll();
    toolWindowService.paintUserMessage(prompt);
    toolWindowService.sendMessage(prompt, project, null);
  }
}

