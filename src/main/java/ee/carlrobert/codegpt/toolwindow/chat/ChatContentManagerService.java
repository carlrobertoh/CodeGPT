package ee.carlrobert.codegpt.toolwindow.chat;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ChatContentManagerService {

  public void displayChatTab(@NotNull Project project) {
    var toolWindow = getToolWindow(project);
    toolWindow.show();
    var contentManager = toolWindow.getContentManager();
    tryFindChatTabContent(contentManager).ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public boolean isChatTabSelected(ContentManager contentManager) {
    return tryFindChatTabContent(contentManager)
        .filter(contentManager::isSelected)
        .isPresent();
  }

  public Optional<ChatTabbedPane> tryFindChatTabbedPane(@NotNull Project project) {
    var chatTabContent = tryFindChatTabContent(getToolWindow(project).getContentManager());
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

  public void resetTabbedPane(@NotNull Project project) {
    tryFindChatTabbedPane(project).ifPresent(tabbedPane -> {
      tabbedPane.removeAll();
      var tabPanel = new ChatToolWindowTabPanel(project);
      tabPanel.displayLandingView();
      tabbedPane.addNewTab(tabPanel);
    });
  }

  private Optional<Content> tryFindChatTabContent(ContentManager contentManager) {
    return Arrays.stream(contentManager.getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }

  private ToolWindow getToolWindow(@NotNull Project project) {
    return requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
  }
}
