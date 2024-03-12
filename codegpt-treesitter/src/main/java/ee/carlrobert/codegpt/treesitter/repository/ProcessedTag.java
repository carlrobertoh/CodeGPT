package ee.carlrobert.codegpt.treesitter.repository;

public class ProcessedTag {

  private final String filePath;
  private final String modifiedContent;

  public ProcessedTag(String filePath, String modifiedContent) {
    this.filePath = filePath;
    this.modifiedContent = modifiedContent;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getModifiedContent() {
    return modifiedContent;
  }
}