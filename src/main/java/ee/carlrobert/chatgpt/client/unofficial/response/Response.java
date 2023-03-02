package ee.carlrobert.chatgpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.chatgpt.client.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response implements ApiResponse {

  private ResponseMessage message;
  @JsonProperty("conversation_id")
  private String conversationId;

  public ResponseMessage getMessage() {
    return message;
  }

  public void setMessage(ResponseMessage message) {
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
