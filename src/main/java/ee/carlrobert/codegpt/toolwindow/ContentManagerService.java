package ee.carlrobert.codegpt.toolwindow;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import ee.carlrobert.codegpt.toolwindow.chat.ChatTabbedPane;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ContentManagerService {

  public void displayChatTab(@NotNull Project project) {
    var toolWindow = requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
    toolWindow.show();
    var contentManager = toolWindow.getContentManager();
    tryFindChatTabContent(contentManager).ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<Content> tryFindChatTabContent(ContentManager contentManager) {
    return Arrays.stream(contentManager.getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }

  public boolean isChatTabSelected(ContentManager contentManager) {
    return tryFindChatTabContent(contentManager)
        .filter(contentManager::isSelected)
        .isPresent();
  }

  public Optional<ChatTabbedPane> tryFindChatTabbedPane(@NotNull Project project) {
    var toolWindow = requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
    var chatTabContent = tryFindChatTabContent(toolWindow.getContentManager());
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
}
