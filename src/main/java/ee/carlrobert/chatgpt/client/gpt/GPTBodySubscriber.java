package ee.carlrobert.chatgpt.client.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.Subscriber;
import ee.carlrobert.chatgpt.client.gpt.response.GPTResponse;
import java.util.function.Consumer;

public class GPTBodySubscriber extends Subscriber<GPTResponse> {

  private final Consumer<String> onCompleteCallback;
  private final StringBuilder messageBuilder = new StringBuilder();

  public GPTBodySubscriber(
      Consumer<GPTResponse> responseConsumer,
      Consumer<String> onCompleteCallback) {
    super(responseConsumer);
    this.onCompleteCallback = onCompleteCallback;
  }

  protected GPTResponse deserializePayload(String jsonPayload) throws JsonProcessingException {
    var response = new ObjectMapper().readValue(jsonPayload, GPTResponse.class);
    messageBuilder.append(response.getChoices().get(0).getText());
    return response;
  }

  protected void onRequestComplete() {
    onCompleteCallback.accept(messageBuilder.toString());
  }
}
