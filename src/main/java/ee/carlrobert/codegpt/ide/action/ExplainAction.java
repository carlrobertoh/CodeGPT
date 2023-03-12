package ee.carlrobert.codegpt.ide.action;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class ExplainAction extends BaseAction {

  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    sendMessage(project, "Explain the following code:\n\n" + selectedText);
  }
}
