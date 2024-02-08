package ee.carlrobert.codegpt.settings.service.ollama.form;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class GetAvailableModelsAction extends AnAction {

  private static final Logger LOG = Logger.getInstance(GetAvailableModelsAction.class);

  private final Runnable beforeStart;
  private final Consumer<List<OllamaModel>> onFinished;
  private final Consumer<Exception> onFailed;

  public GetAvailableModelsAction(
      Runnable beforeStart, Consumer<List<OllamaModel>> onFinished,
      Consumer<Exception> onFailed) {
    this.beforeStart = beforeStart;
    this.onFinished = onFinished;
    this.onFailed = onFailed;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ProgressManager.getInstance().run(new GetAvailableModelsTask(e.getProject()));
  }

  class GetAvailableModelsTask extends Task.Backgroundable {

    GetAvailableModelsTask(Project project) {
      super(
          project,
          CodeGPTBundle.get("settingsConfigurable.service.ollama.updateModelsLink.label"),
          false);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
      ApplicationManager.getApplication().executeOnPooledThread(() -> {
        beforeStart.run();
        try {
          onFinished.accept(CompletionClientProvider.getOllamaClient().getModelTags()
              .getModels());
        } catch (Exception e) {
          onFailed.accept(e);
          return;
        }
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}