package ee.carlrobert.chatgpt.client.chatgpt.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponseError {

  private ChatGPTResponseErrorDetails detail;

  public ChatGPTResponseErrorDetails getDetail() {
    return detail;
  }

  public void setDetail(ChatGPTResponseErrorDetails detail) {
    this.detail = detail;
  }
}
