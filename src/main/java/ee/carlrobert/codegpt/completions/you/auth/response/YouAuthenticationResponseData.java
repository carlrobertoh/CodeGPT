package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouAuthenticationResponseData(
        YouSession session, @JsonProperty("session_jwt") String sessionJwt,
        @JsonProperty("session_token") String sessionToken, YouUser user) {
}
