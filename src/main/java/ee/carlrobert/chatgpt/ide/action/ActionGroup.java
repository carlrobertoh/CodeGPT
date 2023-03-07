package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import icons.Icons;

public class ActionGroup extends DefaultActionGroup {

  @Override
  public void update(AnActionEvent event) {
    event.getPresentation().setIcon(Icons.DefaultIcon);
  }
}
