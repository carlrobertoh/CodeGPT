package ee.carlrobert.chatgpt.client.official.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseChoice {

  private ResponseChoiceDelta delta;

  public ResponseChoiceDelta getDelta() {
    return delta;
  }

  public void setDelta(ResponseChoiceDelta delta) {
    this.delta = delta;
  }
}
