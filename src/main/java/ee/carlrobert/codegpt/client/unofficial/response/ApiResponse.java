package ee.carlrobert.codegpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.codegpt.client.BaseApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse implements BaseApiResponse {

  private final ApiResponseMessage message;
  private final String conversationId;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponse(
      @JsonProperty("message") ApiResponseMessage message,
      @JsonProperty("conversation_id") String conversationId) {
    this.message = message;
    this.conversationId = conversationId;
  }

  public ApiResponseMessage getMessage() {
    return message;
  }

  public String getConversationId() {
    return conversationId;
  }

  public String getFullMessage() {
    return String.join("", message.getContent().getParts());
  }
}
