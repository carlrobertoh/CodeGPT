package ee.carlrobert.codegpt.embeddings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CheckedFile {

  private final String fileName;
  private final String filePath;
  private final String fileContent;

  public CheckedFile(File file) {
    this.fileName = file.getName();
    this.filePath = file.getPath();
    try {
      this.fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getFileName() {
    return fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getFileContent() {
    return fileContent;
  }
}
