package ee.carlrobert.chatgpt.action;

import com.intellij.openapi.wm.ToolWindow;

public class ExplainAction extends BaseAction {

  protected String getPrompt(String selectedText) {
    return "Explain code:\n" + selectedText;
  }

  protected void initToolWindow(ToolWindow toolWindow) {
    toolWindow.setTitle("Explain Code");
    toolWindow.show();
  }
}
