package ee.carlrobert.chatgpt.client.official.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.official.CompletionClientEventListener;
import ee.carlrobert.chatgpt.client.official.text.response.ApiResponse;
import java.util.function.Consumer;

public class TextCompletionClientEventListener extends CompletionClientEventListener {

  public TextCompletionClientEventListener(Consumer<String> onMessageReceived, Consumer<String> onComplete) {
    super(onMessageReceived, onComplete);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, ApiResponse.class)
        .getChoices()
        .get(0)
        .getText();
  }
}
