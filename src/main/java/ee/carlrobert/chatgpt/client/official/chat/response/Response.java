package ee.carlrobert.chatgpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.chatgpt.client.ApiResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response implements ApiResponse {

  private String id;
  private List<ResponseChoice> choices;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ResponseChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<ResponseChoice> choices) {
    this.choices = choices;
  }
}
