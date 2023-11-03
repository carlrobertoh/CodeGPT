package ee.carlrobert.codegpt.completions.llama;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LlamaServerMessage {

  private final String level;
  private final String message;

  public LlamaServerMessage(
      @JsonProperty("level") String level,
      @JsonProperty("message") String message) {
    this.level = level;
    this.message = message;
  }

  public String getLevel() {
    return level;
  }

  public String getMessage() {
    return message;
  }
}
