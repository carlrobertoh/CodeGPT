package ee.carlrobert.chatgpt.client.official.text.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.chatgpt.client.BaseApiResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse implements BaseApiResponse {

  private String id;
  private List<ApiResponseChoice> choices;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ApiResponseChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<ApiResponseChoice> choices) {
    this.choices = choices;
  }
}
