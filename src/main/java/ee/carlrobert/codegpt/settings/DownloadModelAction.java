package ee.carlrobert.codegpt.settings;

import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.util.ApplicationUtils;
import ee.carlrobert.codegpt.util.DownloadingUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultComboBoxModel;
import org.jetbrains.annotations.NotNull;

public class DownloadModelAction extends AnAction {

  private static final Logger LOG = Logger.getInstance(DownloadModelAction.class);

  private final Runnable onDownload;
  private final Runnable onDownloaded;
  private final Consumer<Exception> onFailed;
  private final JBLabel progressLabel;
  private final DefaultComboBoxModel<HuggingFaceModel> comboBoxModel;

  public DownloadModelAction(
      Runnable onDownload,
      Runnable onDownloaded,
      Consumer<Exception> onFailed,
      DefaultComboBoxModel<HuggingFaceModel> comboBoxModel,
      JBLabel progressLabel) {
    this.onDownload = onDownload;
    this.onDownloaded = onDownloaded;
    this.onFailed = onFailed;
    this.comboBoxModel = comboBoxModel;
    this.progressLabel = progressLabel;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ProgressManager.getInstance().run(new Task.Backgroundable(
        ApplicationUtils.findCurrentProject(),
        "Downloading Model",
        true) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        var model = (HuggingFaceModel) comboBoxModel.getSelectedItem();
        URL url;
        try {
          url = new URL(model.getFilePath());
        } catch (MalformedURLException ex) {
          throw new RuntimeException(ex);
        }

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> progressUpdateScheduler = null;

        try {
          onDownload.run();

          indicator.setIndeterminate(false);
          indicator.setText(format("Downloading %s...", model.getFileName()));

          long fileSize = url.openConnection().getContentLengthLong();
          long[] bytesRead = {0};
          long startTime = System.currentTimeMillis();

          progressUpdateScheduler = executorService.scheduleAtFixedRate(() -> {
            progressLabel.setText(
                DownloadingUtils.getFormattedDownloadProgress(startTime, fileSize, bytesRead[0]));
          }, 0, 1, TimeUnit.SECONDS);
          readFile(model.getFileName(), url, bytesRead, fileSize, indicator);


          onDownloaded.run();
        } catch (IOException ex) {
          LOG.error("Unable to open connection", ex);
          onFailed.consume(ex);
        } finally {
          if (progressUpdateScheduler != null) {
            progressUpdateScheduler.cancel(true);
          }
          executorService.shutdown();
        }
      }
    });
  }

  private void readFile(
      String fileName,
      URL url,
      long[] bytesRead,
      long fileSize,
      ProgressIndicator indicator) {
    try (
        var readableByteChannel = Channels.newChannel(url.openStream());
        var fileOutputStream = new FileOutputStream(
            CodeGPTPlugin.getLlamaModelsPath() + File.separator + fileName)) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);

      while (readableByteChannel.read(buffer) != -1) {
        if (indicator.isCanceled()) {
          readableByteChannel.close();
          break;
        }
        buffer.flip();
        bytesRead[0] += fileOutputStream.getChannel().write(buffer);
        buffer.clear();
        indicator.setFraction((double) bytesRead[0] / fileSize);
      }
    } catch (Exception ex) {
      onFailed.consume(ex);
    }
  }
}