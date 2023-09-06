package ee.carlrobert.codegpt.user.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponseData {

  private final Session session;
  private final String sessionJwt;
  private final String sessionToken;
  private final User user;

  public AuthenticationResponseData(
      @JsonProperty("session") Session session,
      @JsonProperty("session_jwt") String sessionJwt,
      @JsonProperty("session_token") String sessionToken,
      @JsonProperty("user") User user) {
    this.session = session;
    this.sessionJwt = sessionJwt;
    this.sessionToken = sessionToken;
    this.user = user;
  }

  public Session getSession() {
    return session;
  }

  public String getSessionJwt() {
    return sessionJwt;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public User getUser() {
    return user;
  }
}
