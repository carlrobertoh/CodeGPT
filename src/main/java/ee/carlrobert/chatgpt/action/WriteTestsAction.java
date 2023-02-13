package ee.carlrobert.chatgpt.action;

import com.intellij.openapi.wm.ToolWindow;

public class WriteTestsAction extends BaseAction {

  protected String getPrompt(String selectedText) {
    return "Generate unit test for the code:\n" + selectedText;
  }

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Write Tests");
    toolWindow.show();
  }
}
