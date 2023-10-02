package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouAuthenticationResponseData {

  private final YouSession session;
  private final String sessionJwt;
  private final String sessionToken;
  private final YouUser user;

  public YouAuthenticationResponseData(
      @JsonProperty("session") YouSession session,
      @JsonProperty("session_jwt") String sessionJwt,
      @JsonProperty("session_token") String sessionToken,
      @JsonProperty("user") YouUser user) {
    this.session = session;
    this.sessionJwt = sessionJwt;
    this.sessionToken = sessionToken;
    this.user = user;
  }

  public YouSession getSession() {
    return session;
  }

  public String getSessionJwt() {
    return sessionJwt;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public YouUser getUser() {
    return user;
  }
}
