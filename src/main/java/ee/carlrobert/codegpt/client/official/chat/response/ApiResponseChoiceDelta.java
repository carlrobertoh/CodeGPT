package ee.carlrobert.codegpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseChoiceDelta {

  private final String role;
  private final String content;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseChoiceDelta(
      @JsonProperty("role") String role,
      @JsonProperty("content") String content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }
}
