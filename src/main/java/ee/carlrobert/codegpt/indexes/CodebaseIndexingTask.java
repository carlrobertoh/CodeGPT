package ee.carlrobert.codegpt.indexes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jelmerk.knn.Item;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import ee.carlrobert.embedding.CheckedFile;
import ee.carlrobert.embedding.EmbeddingsService;
import ee.carlrobert.vector.VectorStore;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CodebaseIndexingTask extends Task.Backgroundable {

  private static final Logger LOG = Logger.getInstance(CodebaseIndexingTask.class);
  private final Project project;
  private final List<CheckedFile> checkedFiles;
  private final EmbeddingsService embeddingsService;

  public CodebaseIndexingTask(Project project, List<CheckedFile> checkedFiles) {
    super(project, CodeGPTBundle.get("codebaseIndexing.task.title"), true);
    this.project = project;
    this.checkedFiles = checkedFiles;
    this.embeddingsService = new EmbeddingsService(
        CompletionClientProvider.getOpenAIClient(),
        CodeGPTPlugin.getPluginBasePath());
  }

  public void run() {
    ProgressManager.getInstance()
        .runProcessWithProgressAsynchronously(this, new BackgroundableProcessIndicator(this));
  }

  @Override
  public void run(@NotNull ProgressIndicator indicator) {
    LOG.info("Indexing started");

    String fileContent;
    try {
      fileContent = new ObjectMapper().writeValueAsString(Map.of("content", checkedFiles));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize json file");
    }

    if (!FileUtil.exists(CodeGPTPlugin.getIndexStorePath())) {
      FileUtils.tryCreateDirectory(CodeGPTPlugin.getIndexStorePath());
    }
    FileUtils.createFile(
        CodeGPTPlugin.getProjectIndexStorePath(project), "index.json", fileContent);

    try {
      indicator.setFraction(0);
      List<Item<Object, double[]>> embeddings =
          embeddingsService.createEmbeddings(checkedFiles, indicator);
      VectorStore.getInstance(CodeGPTPlugin.getPluginBasePath()).save(embeddings);
      OverlayUtils.showNotification("Indexing completed", NotificationType.INFORMATION);

      project.getMessageBus()
          .syncPublisher(CodebaseIndexingCompletedNotifier.INDEXING_COMPLETED_TOPIC)
          .indexingCompleted();
    } catch (RuntimeException e) {
      LOG.error("Something went wrong while indexing the codebase", e);
    } finally {
      if (indicator.isRunning()) {
        indicator.stop();
      }
    }
  }
}
