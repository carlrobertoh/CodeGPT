package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import org.jetbrains.annotations.NotNull;

public class DownloadModelAction extends AnAction {

  private final ModelDownload downloader;
  private final Runnable onDownloaded;
  private final Consumer<Exception> onFailed;
  private final Consumer<String> onUpdateProgress;
  private final DefaultComboBoxModel<HuggingFaceModel> comboBoxModel;

  public DownloadModelAction(
      ModelDownload downloader,
      Consumer<ProgressIndicator> onDownload,
      Runnable onDownloaded,
      Consumer<Exception> onFailed,
      Consumer<String> onUpdateProgress,
      DefaultComboBoxModel<HuggingFaceModel> comboBoxModel) {
    this.downloader = downloader;
    this.onDownloaded = onDownloaded;
    this.onFailed = onFailed;
    this.onUpdateProgress = onUpdateProgress;
    this.comboBoxModel = comboBoxModel;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ProgressManager.getInstance().run(new DownloadBackgroundTask(e.getProject(), downloader));
  }

  class DownloadBackgroundTask extends Task.Backgroundable {

    private final ModelDownload downloader;

    DownloadBackgroundTask(Project project, ModelDownload downloader) {
      super(
          project,
          CodeGPTBundle.get("settingsConfigurable.service.llama.progress.downloadingModel.title"),
          true);
      this.downloader = downloader;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
      var model = (HuggingFaceModel) comboBoxModel.getSelectedItem();
      downloader.download(model, indicator, onUpdateProgress, onFailed);
    }


    @Override
    public void onSuccess() {
      onDownloaded.run();
    }
  }
}