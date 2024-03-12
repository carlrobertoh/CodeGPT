package ee.carlrobert.codegpt.actions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Enables code-completion.<br/> Publishes message to {@link CodeCompletionEnabledListener#TOPIC}
 */
public class EnableCompletionsAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ConfigurationSettings.getCurrentState().setCodeCompletionsEnabled(true);
    ApplicationManager.getApplication()
        .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
        .onCodeCompletionsEnabledChange(true);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
    var codeCompletionEnabled = ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled();
    e.getPresentation().setEnabled(!codeCompletionEnabled);
    e.getPresentation()
        .setVisible(!codeCompletionEnabled && List.of(OPENAI, LLAMA_CPP).contains(selectedService));
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }
}
