package ee.carlrobert.embedding;

import java.util.Set;

public class GeneratedContextDetails {

  private final String context;
  private final Set<String> fileNames;

  public GeneratedContextDetails(String context, Set<String> fileNames) {
    this.context = context;
    this.fileNames = fileNames;
  }

  public String getContext() {
    return context;
  }

  public Set<String> getFileNames() {
    return fileNames;
  }
}
