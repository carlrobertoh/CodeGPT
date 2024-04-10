package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouAuthenticationResponse(YouAuthenticationResponseData data) {
}
