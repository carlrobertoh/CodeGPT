package ee.carlrobert.codegpt.user.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponse {

  private final AuthenticationResponseData data;

  public AuthenticationResponse(@JsonProperty("data") AuthenticationResponseData data) {
    this.data = data;
  }

  public AuthenticationResponseData getData() {
    return data;
  }
}
