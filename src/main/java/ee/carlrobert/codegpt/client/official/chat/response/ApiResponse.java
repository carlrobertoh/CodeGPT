package ee.carlrobert.codegpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.codegpt.client.BaseApiResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse implements BaseApiResponse {

  private final String id;
  private final List<ApiResponseChoice> choices;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponse(
      @JsonProperty("id") String id,
      @JsonProperty("choices") List<ApiResponseChoice> choices) {
    this.id = id;
    this.choices = choices;
  }

  public String getId() {
    return id;
  }

  public List<ApiResponseChoice> getChoices() {
    return choices;
  }
}
