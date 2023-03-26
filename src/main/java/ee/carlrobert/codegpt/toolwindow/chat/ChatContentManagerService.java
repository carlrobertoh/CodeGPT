package ee.carlrobert.codegpt.toolwindow.chat;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import java.util.Arrays;
import java.util.Optional;

@Service(Service.Level.PROJECT)
public class ChatContentManagerService {

  private final Project project;

  public ChatContentManagerService(Project project) {
    this.project = project;
  }

  public ChatToolWindowTabPanel createNewTabPanel() {
    displayChatTab();
    var tabbedPane = tryFindChatTabbedPane();
    if (tabbedPane.isPresent()) {
      var panel = new ChatToolWindowTabPanel(project);
      tabbedPane.get().addNewTab(panel);
      return panel;
    }
    return null;
  }

  public void displayChatTab() {
    var toolWindow = getToolWindow();
    toolWindow.show();
    var contentManager = toolWindow.getContentManager();
    tryFindChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<ChatTabbedPane> tryFindChatTabbedPane() {
    var chatTabContent = tryFindChatTabContent();
    if (chatTabContent.isPresent()) {
      var tabbedPane = Arrays.stream(chatTabContent.get().getComponent().getComponents())
          .filter(component -> component instanceof ChatTabbedPane)
          .findFirst();
      if (tabbedPane.isPresent()) {
        return Optional.of((ChatTabbedPane) tabbedPane.get());
      }
    }
    return Optional.empty();
  }

  public void resetTabbedPane() {
    tryFindChatTabbedPane().ifPresent(tabbedPane -> {
      tabbedPane.clearAll();
      var tabPanel = new ChatToolWindowTabPanel(project);
      tabPanel.displayLandingView();
      tabbedPane.addNewTab(tabPanel);
    });
  }

  private Optional<Content> tryFindChatTabContent() {
    return Arrays.stream(getToolWindow().getContentManager().getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }

  private ToolWindow getToolWindow() {
    return requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
  }
}
