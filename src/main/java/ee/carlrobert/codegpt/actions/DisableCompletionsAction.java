package ee.carlrobert.codegpt.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import ee.carlrobert.codegpt.codecompletions.CodeGPTEditorManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import org.jetbrains.annotations.NotNull;

/**
 * Disables code-completion.<br/> Publishes message to {@link CodeCompletionEnabledListener#TOPIC}
 */
public class DisableCompletionsAction extends DumbAwareAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ConfigurationState.getInstance().setCodeCompletionsEnabled(false);
    CodeGPTEditorManager.getInstance().disposeAllInlays(e.getProject());
    ApplicationManager.getApplication()
        .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
        .onCodeCompletionsEnabledChange(false);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    var codeCompletionEnabled = ConfigurationState.getInstance().isCodeCompletionsEnabled();
    e.getPresentation().setEnabled(codeCompletionEnabled);
    e.getPresentation().setVisible(codeCompletionEnabled);
  }
}
