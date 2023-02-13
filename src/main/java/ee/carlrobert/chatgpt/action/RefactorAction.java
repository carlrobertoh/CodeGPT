package ee.carlrobert.chatgpt.action;

import com.intellij.openapi.wm.ToolWindow;

public class RefactorAction extends BaseAction {

  protected String getPrompt(String selectedText) {
    return "Refactor code:\n" + selectedText;
  }

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Refactor Code");
    toolWindow.show();
  }
}
