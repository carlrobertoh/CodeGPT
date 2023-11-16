package ee.carlrobert.vector;

import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VectorStore {

  private static VectorStore instance;

  private final String storePath;

  private VectorStore(Path pluginPath) {
    this.storePath = getIndexStorePath(pluginPath.toString());
  }

  public static VectorStore getInstance(Path pluginPath) {
    if (instance == null) {
      instance = new VectorStore(pluginPath);
    }
    return instance;
  }

  public HnswIndex<Object, double[], Word, Object> loadIndex() throws IOException {
    return loadIndex(storePath);
  }

  public HnswIndex<Object, double[], Word, Object> loadIndex(String path) throws IOException {
    return HnswIndex.load(new File(path), this.getClass().getClassLoader());
  }

  public void save(List<Item<Object, double[]>> words) {
    var hnswIndex = HnswIndex
        .newBuilder(
            words.get(0).vector().length,
            DistanceFunctions.DOUBLE_COSINE_DISTANCE,
            words.size())
        .build();
    try {
      hnswIndex.addAll(words);
      hnswIndex.save(new File(storePath));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isIndexExists() {
    return FileUtil.exists(storePath);
  }

  private String getIndexStorePath(String pluginBasePath) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      pluginBasePath = new File("src/test/resources/indexes").getAbsolutePath();
    }
    return pluginBasePath + File.separator + "hnsw.index";
  }
}
