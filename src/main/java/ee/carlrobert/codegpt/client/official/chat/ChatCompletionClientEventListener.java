package ee.carlrobert.codegpt.client.official.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.client.official.CompletionClientEventListener;
import ee.carlrobert.codegpt.client.official.chat.response.ApiResponse;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;

public class ChatCompletionClientEventListener extends CompletionClientEventListener {

  public ChatCompletionClientEventListener(
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
        .getDelta()
        .getContent();
  }
}
