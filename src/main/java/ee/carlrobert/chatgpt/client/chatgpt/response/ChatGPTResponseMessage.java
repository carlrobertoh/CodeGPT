package ee.carlrobert.chatgpt.client.chatgpt.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponseMessage {

  private String id;
  private ChatGPTResponseMessageContent content;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ChatGPTResponseMessageContent getContent() {
    return content;
  }

  public void setContent(ChatGPTResponseMessageContent content) {
    this.content = content;
  }
}
