package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouAuthenticationResponse {

  private final YouAuthenticationResponseData data;

  public YouAuthenticationResponse(@JsonProperty("data") YouAuthenticationResponseData data) {
    this.data = data;
  }

  public YouAuthenticationResponseData getData() {
    return data;
  }
}
