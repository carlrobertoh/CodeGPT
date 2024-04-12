package ee.carlrobert.codegpt.completions.you.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouAuthenticationError(@JsonProperty("error_type") String errorType,
                                     @JsonProperty("error_message") String errorMessage) {
}
