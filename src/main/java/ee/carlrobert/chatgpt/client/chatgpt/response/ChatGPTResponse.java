package ee.carlrobert.chatgpt.client.chatgpt.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.chatgpt.client.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponse implements ApiResponse {

  private ChatGPTResponseMessage message;
  @JsonProperty("conversation_id")
  private String conversationId;

  public ChatGPTResponseMessage getMessage() {
    return message;
  }

  public void setMessage(ChatGPTResponseMessage message) {
    this.message = message;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getFullMessage() {
    return String.join("", message.getContent().getParts());
  }
}
