package ee.carlrobert.codegpt.ide.action;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class WriteTestsAction extends BaseAction {

  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    sendMessage(project, "Generate unit tests for the following code:\n\n" + selectedText);
  }
}
