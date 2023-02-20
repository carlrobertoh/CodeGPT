package ee.carlrobert.chatgpt.ide.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class ChatGptToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var content = ApplicationManager.getApplication()
        .getService(ContentFactory.class)
        .createContent(new ChatGptToolWindow(project, toolWindow).getContent(), "", false);
    toolWindow.getContentManager().addContent(content);
  }
}
