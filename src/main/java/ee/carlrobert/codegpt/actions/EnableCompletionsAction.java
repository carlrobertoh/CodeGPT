package ee.carlrobert.codegpt.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import org.jetbrains.annotations.NotNull;

/**
 * Enables code-completion.<br/> Publishes message to {@link CodeCompletionEnabledListener#TOPIC}
 */
public class EnableCompletionsAction extends DumbAwareAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ConfigurationState.getInstance().setCodeCompletionsEnabled(true);
    ApplicationManager.getApplication()
        .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
        .onCodeCompletionsEnabledChange(true);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    var codeCompletionEnabled = ConfigurationState.getInstance().isCodeCompletionsEnabled();
    e.getPresentation().setEnabled(!codeCompletionEnabled);
    e.getPresentation().setVisible(!codeCompletionEnabled);
  }
}
