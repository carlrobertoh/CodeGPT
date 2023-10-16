/*
package ee.carlrobert.codegpt.settings;

import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.util.ApplicationUtils;
import ee.carlrobert.codegpt.util.DownloadingUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class DownloadModelAction extends AnAction {

  private final Runnable onDownloaded;
  private final JBLabel progressLabel;
  private final BorderLayoutPanel downloadModelLinkWrapper;

  DownloadModelAction(
      Runnable onDownloaded,
      JBLabel progressLabel,
      BorderLayoutPanel downloadModelLinkWrapper) {
    this.onDownloaded = onDownloaded;
    this.progressLabel = progressLabel;
    this.downloadModelLinkWrapper = downloadModelLinkWrapper;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ProgressManager.getInstance().run(new Task.Backgroundable(
        ApplicationUtils.findCurrentProject(),
        "Downloading Model",
        true) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        var model = (LlamaModel) modelComboBox.getModel().getSelectedItem();

        URL url;
        try {
          url = new URL(model.getFilePath());
        } catch (MalformedURLException ex) {
          throw new RuntimeException(ex);
        }

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = null;

        try (
            var readableByteChannel = Channels.newChannel(url.openStream());
            var fileOutputStream = new FileOutputStream(
                CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName())) {

          downloadModelLinkWrapper.removeAll();
          downloadModelLinkWrapper.add(progressLabel);
          downloadModelLinkWrapper.repaint();
          downloadModelLinkWrapper.revalidate();

          indicator.setIndeterminate(false);
          indicator.setText(format("Downloading %s...", model.getLabel()));

          long fileSize = url.openConnection().getContentLengthLong();
          long[] bytesRead = {0};
          ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);

          long startTime = System.currentTimeMillis();
          future = executorService.scheduleAtFixedRate(() -> progressLabel.setText(
                  DownloadingUtils.getFormattedDownloadProgress(startTime, fileSize, bytesRead[0])),
              0, 1, TimeUnit.SECONDS);

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

          onDownloaded.run();
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          if (future != null) {
            future.cancel(true);
          }
          executorService.shutdown();
        }
      }
    });
  }
}
*/
