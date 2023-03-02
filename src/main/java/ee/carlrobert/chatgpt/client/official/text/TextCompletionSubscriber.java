package ee.carlrobert.chatgpt.client.official.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.official.CompletionSubscriber;
import ee.carlrobert.chatgpt.client.official.text.response.Response;
import java.util.function.Consumer;

public class TextCompletionSubscriber extends CompletionSubscriber {

  public TextCompletionSubscriber(Consumer<String> responseConsumer, Consumer<String> onCompleteCallback) {
    super(responseConsumer, onCompleteCallback);
  }

  protected String getMessage(String responsePayload) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(responsePayload, Response.class)
        .getChoices()
        .get(0)
        .getText();
  }
}
