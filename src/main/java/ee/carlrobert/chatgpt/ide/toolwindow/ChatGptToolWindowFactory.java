package ee.carlrobert.chatgpt.ide.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import ee.carlrobert.chatgpt.ide.conversations.ConversationsState;
import ee.carlrobert.chatgpt.ide.toolwindow.conversations.ConversationsToolWindow;
import java.util.Arrays;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ChatGptToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    addContent(toolWindow, new ChatGptToolWindow(project).getContent(), "Chat");

    var conversationToolWidow = new ConversationsToolWindow(project, toolWindow);
    addContent(toolWindow, conversationToolWidow.getContent(), "Conversation History");
    toolWindow.addContentManagerListener(new ContentManagerListener() {
      public void selectionChanged(@NotNull ContentManagerEvent event) {
        if ("Conversation History".equals(event.getContent().getTabName()) && event.getContent().isSelected()) {
          conversationToolWidow.refresh();
        }
      }
    });

    var conversation = ConversationsState.getCurrentConversation();
    if (conversation != null) {
      var toolWindowService = project.getService(ToolWindowService.class);
      var contentManager = toolWindow.getContentManager();
      Arrays.stream(contentManager.getContents())
          .filter(content -> "Chat".equals(content.getTabName()))
          .findFirst()
          .ifPresent(
              content -> {
                if (contentManager.isSelected(content)) {
                  toolWindowService.displayConversation(conversation);
                }
              }
          );
    }
  }

  private void addContent(@NotNull ToolWindow toolWindow, JPanel content, String displayName) {
    toolWindow.getContentManager().addContent(ApplicationManager.getApplication()
        .getService(ContentFactory.class)
        .createContent(content, displayName, false));
  }
}
