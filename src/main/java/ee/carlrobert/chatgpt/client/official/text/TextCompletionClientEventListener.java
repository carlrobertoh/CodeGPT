package ee.carlrobert.chatgpt.client.official.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.official.CompletionClientEventListener;
import ee.carlrobert.chatgpt.client.official.text.response.ApiResponse;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;

public class TextCompletionClientEventListener extends CompletionClientEventListener {

  public TextCompletionClientEventListener(
      OkHttpClient client,
      Consumer<String> onMessageReceived,
      Consumer<String> onComplete,
      Consumer<String> onFailure) {
    super(client, onMessageReceived, onComplete, onFailure);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, ApiResponse.class)
        .getChoices()
        .get(0)
        .getText();
  }
}
