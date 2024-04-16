package ee.carlrobert.codegpt.settings.service.llama.form

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.completions.HuggingFaceModel
import ee.carlrobert.codegpt.util.DownloadingUtil
import ee.carlrobert.codegpt.util.file.FileUtil.copyFileWithProgress
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import javax.swing.DefaultComboBoxModel

class DownloadModelAction(
  private val onDownload: Consumer<ProgressIndicator>,
  private val onDownloaded: Runnable,
  private val onFailed: Consumer<Exception>,
  private val onUpdateProgress: Consumer<String>,
  private val comboBoxModel: DefaultComboBoxModel<HuggingFaceModel>
) : AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    ProgressManager.getInstance().run(DownloadBackgroundTask(e.project))
  }

  internal inner class DownloadBackgroundTask(project: Project?) : Task.Backgroundable(
    project,
    CodeGPTBundle.get("settingsConfigurable.service.llama.progress.downloadingModel.title"),
    true
  ) {
    override fun run(indicator: ProgressIndicator) {
      val model = comboBoxModel.selectedItem as HuggingFaceModel
      val urls = model.fileURLs
      val numberOfFiles = urls.size
      var errorOccured = false
      for (i in 1..numberOfFiles + 1) {
        if (errorOccured || indicator.isCanceled) {
          break
        }
        val executorService = Executors.newSingleThreadScheduledExecutor()
        var progressUpdateScheduler: ScheduledFuture<*>? = null
        val url = urls[i - 1]

        try {
          onDownload.accept(indicator)

          indicator.isIndeterminate = false
          indicator.text = String.format(
            CodeGPTBundle.get(
              "settingsConfigurable.service.llama.progress.downloadingModelIndicator.text"
            ),
            model.fileNames[i - 1]
          )

          val fileSize = url.openConnection().contentLengthLong
          val bytesRead = longArrayOf(0)
          val startTime = System.currentTimeMillis()

          progressUpdateScheduler = executorService.scheduleAtFixedRate(
            {
              onUpdateProgress.accept(
                DownloadingUtil.getFormattedDownloadProgress(
                  i,
                  numberOfFiles,
                  startTime,
                  fileSize,
                  bytesRead[0]
                )
              )
            },
            0, 1, TimeUnit.SECONDS
          )
          copyFileWithProgress(model.fileNames[i - 1], url, bytesRead, fileSize, indicator)
        } catch (ex: IOException) {
          LOG.error("Unable to download", ex, url.toString())
          onFailed.accept(ex)
          errorOccured = true
        } finally {
          progressUpdateScheduler?.cancel(true)
          executorService.shutdown()
        }
      }
    }

    override fun onSuccess() {
      onDownloaded.run()
    }
  }

  companion object {
    private val LOG = Logger.getInstance(DownloadModelAction::class.java)
  }
}
