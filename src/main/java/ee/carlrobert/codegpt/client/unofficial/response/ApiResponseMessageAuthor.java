package ee.carlrobert.codegpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseMessageAuthor {

  private final String role;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseMessageAuthor(@JsonProperty("role") String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}
