package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.wm.ToolWindow;

public class OptimizeAction extends BaseAction {

  protected String getPrompt(String selectedText) {
    return "Optimize the following code:\n" + selectedText;
  }

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Optimize Code");
    toolWindow.show();
  }
}
