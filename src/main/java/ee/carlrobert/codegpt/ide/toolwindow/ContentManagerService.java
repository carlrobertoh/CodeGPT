package ee.carlrobert.codegpt.ide.toolwindow;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ContentManagerService {

  private static ContentManagerService instance;
  private final ContentManager contentManager;

  private ContentManagerService(Project project) {
    this.contentManager = requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT")).getContentManager();
  }

  public static ContentManagerService getInstance(@NotNull Project project) {
    if (instance == null) {
      instance = new ContentManagerService(project);
    }
    return instance;
  }

  public void addContent(JPanel content, String displayName) {
    contentManager.addContent(ApplicationManager.getApplication()
        .getService(ContentFactory.class)
        .createContent(content, displayName, false));
  }

  public void displayChatTab() {
    tryFindChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<Content> tryFindChatTabContent() {
    return Arrays.stream(contentManager.getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }

  public boolean isChatTabSelected() {
    return tryFindChatTabContent()
        .filter(contentManager::isSelected)
        .isPresent();
  }
}
