package ee.carlrobert.chatgpt.client.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.Subscriber;
import ee.carlrobert.chatgpt.client.gpt.response.GPTResponse;
import java.util.function.Consumer;

public class GPTBodySubscriber extends Subscriber<GPTResponse> {

  private final Consumer<String> onCompleteCallback;
  private final StringBuilder messageBuilder = new StringBuilder();
  private final Consumer<String> responseConsumer;

  public GPTBodySubscriber(
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
        var response = new ObjectMapper().readValue(responsePayload, GPTResponse.class);
        var message = response.getChoices().get(0).getText();
        messageBuilder.append(message);
        this.responseConsumer.accept(message);
      }
    } catch (JsonProcessingException e) {
      future.completeExceptionally(e);
    }
  }
}
