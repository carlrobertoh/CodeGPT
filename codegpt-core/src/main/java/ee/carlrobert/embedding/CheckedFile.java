package ee.carlrobert.embedding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  public String getFileExtension() {
    Pattern pattern = Pattern.compile("[^.]+$");
    Matcher matcher = pattern.matcher(fileName);

    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }
}
