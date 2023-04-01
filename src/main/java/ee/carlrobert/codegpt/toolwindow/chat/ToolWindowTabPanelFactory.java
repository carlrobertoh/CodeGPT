package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefApp;
import org.jetbrains.annotations.NotNull;

public class ToolWindowTabPanelFactory {

  public static ToolWindowTabPanel getTabPanel(@NotNull Project project) {
    if (JBCefApp.isSupported()) {
      return new ChatToolWindowTabHtmlPanel(project);
    }
    return new ChatToolWindowTabPanel(project);
  }
}
