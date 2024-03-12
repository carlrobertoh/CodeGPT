package ee.carlrobert.codegpt.treesitter.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.Objects;

public class Tag {

  private final String filePath;
  private final String name;
  private final String symbolName;
  private final TagType type;

  @JsonCreator
  public Tag(
      @JsonProperty("filePath") String filePath,
      @JsonProperty("name") String name,
      @JsonProperty("symbolName") String symbolName,
      @JsonProperty("type") TagType type) {
    this.filePath = filePath;
    this.name = name;
    this.symbolName = symbolName;
    this.type = type;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getName() {
    return name;
  }

  public String getSymbolName() {
    return symbolName;
  }

  public TagType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Tag{" +
        "fileName='" + Path.of(filePath).getFileName().toString() + '"' +
        "name='" + name + '"' +
        ", symbolName='" + symbolName + '"' +
        '}' + "\n";
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tag)) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equals(filePath, tag.filePath)
        && Objects.equals(name, tag.name)
        && Objects.equals(symbolName, tag.symbolName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filePath, name, symbolName);
  }
}
