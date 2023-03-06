package ee.carlrobert.chatgpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseChoice {

  private ApiResponseChoiceDelta delta;

  public ApiResponseChoiceDelta getDelta() {
    return delta;
  }

  public void setDelta(ApiResponseChoiceDelta delta) {
    this.delta = delta;
  }
}
