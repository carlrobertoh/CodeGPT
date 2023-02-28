package ee.carlrobert.chatgpt.client.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.Subscriber;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponse;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponseDetail;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponseError;
import java.util.function.Consumer;

public class ChatGPTBodySubscriber extends Subscriber<ChatGPTResponse> {

  private final Consumer<String> responseConsumer;
  private final Consumer<ChatGPTResponse> onCompleteCallback;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private ChatGPTResponse lastReceivedResponse;

  public ChatGPTBodySubscriber(
      Consumer<String> responseConsumer,
      Consumer<ChatGPTResponse> onCompleteCallback) {
    this.responseConsumer = responseConsumer;
    this.onCompleteCallback = onCompleteCallback;
  }

  protected void onRequestComplete() {
    onCompleteCallback.accept(lastReceivedResponse);
  }

  protected void onErrorOccurred() {
    responseConsumer.accept("Something went wrong. Please try again later.");
  }

  protected void send(String responsePayload, String token) {
    if (!responsePayload.isEmpty()) {
      try {
        var response = objectMapper.readValue(responsePayload, ChatGPTResponse.class);
        var author = response.getMessage().getAuthor();
        if (author != null && "assistant".equals(author.getRole())) {
          var message = response.getFullMessage();
          if (lastReceivedResponse != null) {
            message = message.replace(lastReceivedResponse.getFullMessage(), "");
          }
          lastReceivedResponse = response;
          this.responseConsumer.accept(message);
        }
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to deserialize the payload", e);
      }
    } else {
      try {
        var response = objectMapper.readValue(token, ChatGPTResponseDetail.class);
        this.responseConsumer.accept(response.getDetail());
      } catch (JsonProcessingException e) {
        tryProcessingErrorResponse(token);
      }
    }
  }

  private void tryProcessingErrorResponse(String jsonPayload) {
    try {
      var error = objectMapper.readValue(jsonPayload, ChatGPTResponseError.class);
      if ("invalid_api_key".equals(error.getDetail().getCode())) {
        responseConsumer.accept(error.getDetail().getMessage());
      }
      future.complete(null);
    } catch (JsonProcessingException e) {
      future.completeExceptionally(e);
    }
  }
}
