package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class ExplainAction extends BaseAction {

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Explain Code");
    toolWindow.show();
  }

  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    sendMessage(project, "Explain the following code:\n\n" + selectedText);
  }
}
