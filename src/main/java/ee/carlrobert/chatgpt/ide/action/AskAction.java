package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.chatgpt.ide.conversations.ConversationsState;
import ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowService;
import java.util.Arrays;
import java.util.Objects;
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

      toolWindow.show();
      toolWindow.setTitle("Chat");
      toolWindowService.removeAll();
      toolWindowService.paintLandingView();
    }
  }
}
