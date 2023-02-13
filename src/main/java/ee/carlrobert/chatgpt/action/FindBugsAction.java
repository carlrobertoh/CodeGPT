package ee.carlrobert.chatgpt.action;

import com.intellij.openapi.wm.ToolWindow;

public class FindBugsAction extends BaseAction {

  protected String getPrompt(String selectedText) {
    return "Find bugs in the code:\n" + selectedText;
  }

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Find Bugs");
    toolWindow.show();
  }
}
