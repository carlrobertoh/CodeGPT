package ee.carlrobert.codegpt.embeddings;

import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VectorStore {

  private static VectorStore instance;

  private final String indexPath;
  private final Project project;

  private VectorStore(Project project) {
    this.project = project;
    this.indexPath = getIndexStorePath(project);
  }

  public static VectorStore getInstance(Project project) {
    if (instance == null) {
      instance = new VectorStore(project);
    }
    return instance;
  }

  public HnswIndex<Object, double[], Word, Object> loadIndex() throws IOException {
    return loadIndex(indexPath);
  }

  public HnswIndex<Object, double[], Word, Object> loadIndex(String path) throws IOException {
    return HnswIndex.load(new File(path), this.getClass().getClassLoader());
  }

  public void save(List<Item<Object, double[]>> words) {
    var hnswIndex = HnswIndex
        .newBuilder(words.get(0).vector().length, DistanceFunctions.DOUBLE_COSINE_DISTANCE, words.size())
        .build();
    try {
      hnswIndex.addAll(words);
      hnswIndex.save(new File(indexPath));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isIndexExists() {
    return FileUtil.exists(CodeGPTPlugin.getProjectIndexPath(project));
  }

  private String getIndexStorePath(Project project) {
    var basePath = CodeGPTPlugin.getProjectIndexStorePath(project);
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      basePath = new File("src/test/resources/indexes").getAbsolutePath();
    }
    return basePath + File.separator + "hnsw.index";
  }
}
