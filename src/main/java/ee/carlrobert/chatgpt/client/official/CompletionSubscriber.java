package ee.carlrobert.chatgpt.client.official;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.chatgpt.client.ApiResponse;
import ee.carlrobert.chatgpt.client.Subscriber;
import java.util.function.Consumer;

public abstract class CompletionSubscriber extends Subscriber<ApiResponse> {

  private final StringBuilder messageBuilder = new StringBuilder();
  private final Consumer<String> onCompleteCallback;
  private final Consumer<String> responseConsumer;

  protected abstract String getMessage(String responsePayload) throws JsonProcessingException;

  public CompletionSubscriber(
      Consumer<String> responseConsumer,
      Consumer<String> onCompleteCallback) {
    this.responseConsumer = responseConsumer;
    this.onCompleteCallback = onCompleteCallback;
  }

  protected void onRequestComplete() {
    onCompleteCallback.accept(messageBuilder.toString());
  }

  protected void onErrorOccurred() {
    responseConsumer.accept("Something went wrong. Please try again later.");
  }

  protected void send(String responsePayload, String token) {
    try {
      if (!responsePayload.isEmpty()) {
        var message = getMessage(responsePayload);
        messageBuilder.append(message);
        this.responseConsumer.accept(message);
      }
    } catch (JsonProcessingException e) {
      future.completeExceptionally(e);
    }
  }
}
