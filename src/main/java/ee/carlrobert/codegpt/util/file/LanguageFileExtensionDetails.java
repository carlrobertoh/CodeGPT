package ee.carlrobert.codegpt.util.file;

import java.util.List;

public class LanguageFileExtensionDetails {

  private String name;
  private String type;
  private List<String> extensions;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getExtensions() {
    return extensions;
  }

  public void setExtensions(List<String> extensions) {
    this.extensions = extensions;
  }
}
