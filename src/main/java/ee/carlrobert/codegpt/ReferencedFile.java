package ee.carlrobert.codegpt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferencedFile {

  private final String fileName;
  private final String filePath;
  private final String fileContent;

  public ReferencedFile(File file) {
    this.fileName = file.getName();
    this.filePath = file.getPath();
    try {
      this.fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ReferencedFile(String fileName, String filePath, String fileContent) {
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileContent = fileContent;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReferencedFile that = (ReferencedFile) o;
    return Objects.equals(filePath, that.filePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filePath);
  }
}
