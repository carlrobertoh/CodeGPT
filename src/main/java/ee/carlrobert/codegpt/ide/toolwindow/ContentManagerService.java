package ee.carlrobert.codegpt.ide.toolwindow;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ContentManagerService {

  private ToolWindow toolWindow;

  public void setToolWindow(@NotNull ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
  }

  public void addContent(JPanel panel, String displayName) {
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, displayName, false);
    toolWindow.getContentManager().addContent(content);
  }

  public void displayChatTab() {
    var contentManager = getContentManager();
    tryFindChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(getContentManager().getContent(0)))
    );
  }

  public Optional<Content> tryFindChatTabContent() {
    return Arrays.stream(getContentManager().getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }

  public boolean isChatTabSelected() {
    return tryFindChatTabContent()
        .filter(content -> getContentManager().isSelected(content))
        .isPresent();
  }

  private ContentManager getContentManager() {
    return toolWindow.getContentManager();
  }
}
