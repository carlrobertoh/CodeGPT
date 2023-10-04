package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouSession {

  private final String expiresAt;
  private final String lastAccessedAt;
  private final String sessionId;
  private final String startedAt;
  private final String userId;

  public YouSession(
      @JsonProperty("expires_at") String expiresAt,
      @JsonProperty("last_accessed_at") String lastAccessedAt,
      @JsonProperty("session_id") String sessionId,
      @JsonProperty("started_at") String startedAt,
      @JsonProperty("user_id") String userId) {
    this.expiresAt = expiresAt;
    this.lastAccessedAt = lastAccessedAt;
    this.sessionId = sessionId;
    this.startedAt = startedAt;
    this.userId = userId;
  }

  public String getExpiresAt() {
    return expiresAt;
  }

  public String getLastAccessedAt() {
    return lastAccessedAt;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public String getUserId() {
    return userId;
  }
}
