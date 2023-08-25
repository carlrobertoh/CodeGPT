package ee.carlrobert.codegpt.user.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {

  private final String accessToken;
  private final String refreshToken;
  private final SessionUser user;
  private final long expiresIn;
  private final LocalDateTime createdAt;

  public Session(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("refresh_token") String refreshToken,
      @JsonProperty("user") SessionUser user,
      @JsonProperty("expires_in") long expiresIn) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.user = user;
    this.expiresIn = expiresIn;
    this.createdAt = LocalDateTime.now();
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public SessionUser getUser() {
    return user;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(createdAt.plusSeconds(expiresIn));
  }
}
