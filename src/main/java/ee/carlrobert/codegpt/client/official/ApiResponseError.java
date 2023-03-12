package ee.carlrobert.codegpt.client.official;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseError {

  private final ErrorDetails error;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseError(@JsonProperty("error") ErrorDetails error) {
    this.error = error;
  }

  public ErrorDetails getError() {
    return error;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class ErrorDetails {
    private final String message;
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ErrorDetails(@JsonProperty("message") String message, @JsonProperty("type") String type) {
      this.message = message;
      this.type = type;
    }

    public String getMessage() {
      return message;
    }

    public String getType() {
      return type;
    }
  }
}
