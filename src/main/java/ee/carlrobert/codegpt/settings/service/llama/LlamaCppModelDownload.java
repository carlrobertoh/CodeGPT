package ee.carlrobert.codegpt.settings.service.llama;

import static java.lang.String.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.service.ServicesSelectionForm;
import ee.carlrobert.codegpt.util.DownloadingUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LlamaCppModelDownload implements ModelDownload {
  private static final Logger LOG = Logger.getInstance(LlamaCppModelDownload.class);

  @Override
  public void download(HuggingFaceModel model, ProgressIndicator indicator,
      Consumer<String> onUpdateProgress,
      Consumer<Exception> onFailed) {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> progressUpdateScheduler = null;
    try {
      indicator.setIndeterminate(false);
      indicator.setText(format(
          CodeGPTBundle.get(
              "settingsConfigurable.service.llama.progress.downloadingModelIndicator.text"),
          model.getModelFileName()));

      URL url = model.getModelUrl();
      long fileSize = url.openConnection().getContentLengthLong();
      long[] bytesRead = {0};
      long startTime = System.currentTimeMillis();

      progressUpdateScheduler = executorService.scheduleAtFixedRate(() ->
              onUpdateProgress.accept(DownloadingUtil.getFormattedDownloadProgress(
                  startTime,
                  fileSize,
                  bytesRead[0])),
          0, 1, TimeUnit.SECONDS);
      FileUtil.copyFileWithProgress(model.getModelFileName(), url, bytesRead,
          fileSize, indicator);
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

}