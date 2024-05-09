package ee.carlrobert.codegpt.actions;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ServiceConfigurable;
import org.jetbrains.annotations.NotNull;

public class OpenSettingsAction extends AnAction {

  public OpenSettingsAction() {
    super(CodeGPTBundle.get("action.openSettings.title"),
        CodeGPTBundle.get("action.openSettings.description"),
        General.Settings);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), ServiceConfigurable.class);
  }
}
