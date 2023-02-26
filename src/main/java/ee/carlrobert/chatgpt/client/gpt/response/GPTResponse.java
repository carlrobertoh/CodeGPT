package ee.carlrobert.chatgpt.client.gpt.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.chatgpt.client.ApiResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GPTResponse implements ApiResponse {

  private String id;
  private List<GPTResponseChoice> choices;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<GPTResponseChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<GPTResponseChoice> choices) {
    this.choices = choices;
  }
}
