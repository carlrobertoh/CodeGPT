package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouSession(
        @JsonProperty("expires_at") String expiresAt,
        @JsonProperty("last_accessed_at") String lastAccessedAt,
        @JsonProperty("session_id") String sessionId,
        @JsonProperty("started_at") String startedAt,
        @JsonProperty("user_id") String userId) {
}
