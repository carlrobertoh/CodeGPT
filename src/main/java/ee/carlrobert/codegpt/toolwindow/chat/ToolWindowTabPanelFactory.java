package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefApp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolWindowTabPanelFactory {

  public static ToolWindowTabPanel getTabPanel(@NotNull Project project, @Nullable Editor editor) {
    if (JBCefApp.isSupported()) {
      return new ChatToolWindowTabHtmlPanel(editor);
    }
    return new ChatToolWindowTabPanel(project);
  }
}
