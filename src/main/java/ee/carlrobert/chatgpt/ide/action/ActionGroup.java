package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import icons.SdkIcons;

public class ActionGroup extends DefaultActionGroup {

  @Override
  public void update(AnActionEvent event) {
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    event.getPresentation().setIcon(SdkIcons.Sdk_default_icon);
    Project project = event.getProject();
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      var secretKey = SettingsState.getInstance().secretKey;
      menuAllowed = secretKey != null && !secretKey.isEmpty() && editor.getSelectionModel().getSelectedText() != null;
    }
    event.getPresentation().setEnabled(menuAllowed);
  }
}
