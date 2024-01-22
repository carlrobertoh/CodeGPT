package ee.carlrobert.codegpt.actions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import org.jetbrains.annotations.NotNull;

public class DisableCompletionsAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ConfigurationState.getInstance().setCodeCompletionsEnabled(false);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    var codeCompletionEnabled = ConfigurationState.getInstance().isCodeCompletionsEnabled();
    e.getPresentation().setEnabled(codeCompletionEnabled
        && LLAMA_CPP.equals(SettingsState.getInstance().getSelectedService()));
    e.getPresentation().setVisible(codeCompletionEnabled);
  }
}
