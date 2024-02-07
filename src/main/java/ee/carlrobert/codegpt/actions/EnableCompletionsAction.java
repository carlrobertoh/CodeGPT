package ee.carlrobert.codegpt.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
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
    var codeCompletionEnabled = ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled();
    e.getPresentation().setEnabled(!codeCompletionEnabled);
    e.getPresentation().setVisible(!codeCompletionEnabled);
  }
}
