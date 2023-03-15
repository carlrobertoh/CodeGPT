package ee.carlrobert.codegpt.ide.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.toolwindow.conversations.ConversationsToolWindow;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ProjectToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var chatToolWindow = new ChatGptToolWindow(project);
    var conversationsToolWindow = new ConversationsToolWindow(project);
    var toolWindowService = project.getService(ToolWindowService.class);
    toolWindowService.setChatToolWindow(chatToolWindow);

    var contentManagerService = project.getService(ContentManagerService.class);
    addContent(toolWindow, chatToolWindow.getContent(), "Chat");
    addContent(toolWindow, conversationsToolWindow.getContent(), "Conversation History");
    toolWindow.addContentManagerListener(new ContentManagerListener() {
      public void selectionChanged(@NotNull ContentManagerEvent event) {
        var content = event.getContent();
        if ("Conversation History".equals(content.getTabName()) && content.isSelected()) {
          conversationsToolWindow.refresh();
        }
      }
    });

    if (contentManagerService.isChatTabSelected(toolWindow.getContentManager())) {
      var conversation = ConversationsState.getCurrentConversation();
      if (conversation == null) {
        chatToolWindow.displayLandingView();
      } else {
        chatToolWindow.displayConversation(conversation);
      }
    }
  }

  public void addContent(ToolWindow toolWindow, JPanel panel, String displayName) {
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, displayName, false);
    toolWindow.getContentManager().addContent(content);
  }
}
