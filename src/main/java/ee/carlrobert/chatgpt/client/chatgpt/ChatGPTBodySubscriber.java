package ee.carlrobert.chatgpt.client.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.Subscriber;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponse;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponseError;
import java.util.function.Consumer;

public class ChatGPTBodySubscriber extends Subscriber<ChatGPTResponse> {

  private final Consumer<ChatGPTResponse> onCompleteCallback;
  private final Consumer<ChatGPTResponseError> onErrorCallback;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private ChatGPTResponse lastReceivedResponse;

  public ChatGPTBodySubscriber(
      Consumer<ChatGPTResponse> responseConsumer,
      Consumer<ChatGPTResponse> onCompleteCallback,
      Consumer<ChatGPTResponseError> onErrorCallback) {
    super(responseConsumer);
    this.onCompleteCallback = onCompleteCallback;
    this.onErrorCallback = onErrorCallback;
  }

  protected ChatGPTResponse deserializePayload(String jsonPayload) throws JsonProcessingException {
    lastReceivedResponse = objectMapper.readValue(jsonPayload, ChatGPTResponse.class);
    return lastReceivedResponse;
  }

  protected void onRequestComplete() {
    onCompleteCallback.accept(lastReceivedResponse);
  }


  protected void processRegularResponse(String jsonPayload) {
    try {
      onErrorCallback.accept(objectMapper.readValue(jsonPayload, ChatGPTResponseError.class));
      future.complete(null);
    } catch (JsonProcessingException e) {
      future.completeExceptionally(e);
    }
  }
}
