package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import icons.Icons;

public class ActionGroup extends DefaultActionGroup {

  @Override
  public void update(AnActionEvent event) {
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    event.getPresentation().setIcon(Icons.DefaultIcon);
    Project project = event.getProject();
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      menuAllowed = editor.getSelectionModel().getSelectedText() != null;
    }
    event.getPresentation().setEnabled(menuAllowed);
  }
}
