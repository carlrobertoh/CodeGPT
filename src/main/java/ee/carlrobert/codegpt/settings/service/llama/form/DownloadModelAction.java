package ee.carlrobert.codegpt.settings.service.llama.form;

import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.util.DownloadingUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import org.jetbrains.annotations.NotNull;

public class DownloadModelAction extends AnAction {

  private static final Logger LOG = Logger.getInstance(DownloadModelAction.class);

  private final Consumer<ProgressIndicator> onDownload;
  private final Runnable onDownloaded;
  private final Consumer<Exception> onFailed;
  private final Consumer<String> onUpdateProgress;
  private final DefaultComboBoxModel<HuggingFaceModel> comboBoxModel;

  public DownloadModelAction(
      Consumer<ProgressIndicator> onDownload,
      Runnable onDownloaded,
      Consumer<Exception> onFailed,
      Consumer<String> onUpdateProgress,
      DefaultComboBoxModel<HuggingFaceModel> comboBoxModel) {
    this.onDownload = onDownload;
    this.onDownloaded = onDownloaded;
    this.onFailed = onFailed;
    this.onUpdateProgress = onUpdateProgress;
    this.comboBoxModel = comboBoxModel;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ProgressManager.getInstance().run(new DownloadBackgroundTask(e.getProject()));
  }

  class DownloadBackgroundTask extends Task.Backgroundable {

    DownloadBackgroundTask(Project project) {
      super(
          project,
          CodeGPTBundle.get("settingsConfigurable.service.llama.progress.downloadingModel.title"),
          true);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
      var model = (HuggingFaceModel) comboBoxModel.getSelectedItem();
      URL url = model.getFileURL();
      ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
      ScheduledFuture<?> progressUpdateScheduler = null;

      try {
        onDownload.accept(indicator);

        indicator.setIndeterminate(false);
        indicator.setText(format(
            CodeGPTBundle.get(
                "settingsConfigurable.service.llama.progress.downloadingModelIndicator.text"),
            model.getFileName()));

        long fileSize = url.openConnection().getContentLengthLong();
        long[] bytesRead = {0};
        long startTime = System.currentTimeMillis();

        progressUpdateScheduler = executorService.scheduleAtFixedRate(() ->
                onUpdateProgress.accept(DownloadingUtil.getFormattedDownloadProgress(
                    startTime,
                    fileSize,
                    bytesRead[0])),
            0, 1, TimeUnit.SECONDS);
        FileUtil.copyFileWithProgress(model.getFileName(), url, bytesRead, fileSize, indicator);
      } catch (IOException ex) {
        LOG.error("Unable to open connection", ex);
        onFailed.accept(ex);
      } finally {
        if (progressUpdateScheduler != null) {
          progressUpdateScheduler.cancel(true);
        }
        executorService.shutdown();
      }
    }

    @Override
    public void onSuccess() {
      onDownloaded.run();
    }
  }
}