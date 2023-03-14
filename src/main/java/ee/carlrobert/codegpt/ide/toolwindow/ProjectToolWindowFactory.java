package ee.carlrobert.codegpt.ide.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.toolwindow.conversations.ConversationsToolWindow;
import org.jetbrains.annotations.NotNull;

public class ProjectToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var toolWindowService = project.getService(ToolWindowService.class);
    var chatToolWindow = new ChatGptToolWindow(project, toolWindow);
    var conversationsToolWindow = new ConversationsToolWindow(project);
    toolWindowService.setChatToolWindow(chatToolWindow);

    var contentManagerService = project.getService(ContentManagerService.class);
    contentManagerService.setToolWindow(toolWindow);
    contentManagerService.addContent(chatToolWindow.getContent(), "Chat");
    contentManagerService.addContent(conversationsToolWindow.getContent(), "Conversation History");
    toolWindow.addContentManagerListener(new ContentManagerListener() {
      public void selectionChanged(@NotNull ContentManagerEvent event) {
        var content = event.getContent();
        if ("Conversation History".equals(content.getTabName()) && content.isSelected()) {
          conversationsToolWindow.refresh();
        }
      }
    });

    if (contentManagerService.isChatTabSelected()) {
      var conversation = ConversationsState.getCurrentConversation();
      if (conversation == null) {
        chatToolWindow.displayLandingView();
      } else {
        chatToolWindow.displayConversation(conversation);
      }
    }
  }
}
