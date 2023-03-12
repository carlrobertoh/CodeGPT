package ee.carlrobert.codegpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseChoice {

  private final ApiResponseChoiceDelta delta;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseChoice(@JsonProperty("delta") ApiResponseChoiceDelta delta) {
    this.delta = delta;
  }

  public ApiResponseChoiceDelta getDelta() {
    return delta;
  }
}
