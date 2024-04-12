package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouEmail(String email, @JsonProperty("email_id") String emailId, boolean verified) {
}
