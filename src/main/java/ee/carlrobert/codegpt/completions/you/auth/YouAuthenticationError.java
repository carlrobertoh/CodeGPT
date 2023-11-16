package ee.carlrobert.codegpt.completions.you.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouAuthenticationError {

  private final String errorType;
  private final String errorMessage;

  public YouAuthenticationError(
      @JsonProperty("error_type") String errorType,
      @JsonProperty("error_message") String errorMessage) {
    this.errorType = errorType;
    this.errorMessage = errorMessage;
  }

  public String getErrorType() {
    return errorType;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
