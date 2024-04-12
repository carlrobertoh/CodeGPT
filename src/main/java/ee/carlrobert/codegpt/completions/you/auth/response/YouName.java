package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouName(@JsonProperty("first_name") String firstName,
                      @JsonProperty("middle_name") String middleName,
                      @JsonProperty("last_name") String lastName) {
}
