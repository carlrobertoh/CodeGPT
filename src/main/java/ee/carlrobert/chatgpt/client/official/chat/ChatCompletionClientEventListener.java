package ee.carlrobert.chatgpt.client.official.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.official.CompletionClientEventListener;
import ee.carlrobert.chatgpt.client.official.chat.response.ApiResponse;
import java.util.function.Consumer;

public class ChatCompletionClientEventListener extends CompletionClientEventListener {

  public ChatCompletionClientEventListener(Consumer<String> onMessageReceived, Consumer<String> onComplete) {
    super(onMessageReceived, onComplete);
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
