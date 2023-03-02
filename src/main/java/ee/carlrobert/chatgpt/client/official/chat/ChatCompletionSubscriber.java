package ee.carlrobert.chatgpt.client.official.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.official.CompletionSubscriber;
import ee.carlrobert.chatgpt.client.official.chat.response.Response;
import java.util.function.Consumer;

public class ChatCompletionSubscriber extends CompletionSubscriber {

  public ChatCompletionSubscriber(Consumer<String> responseConsumer, Consumer<String> onCompleteCallback) {
    super(responseConsumer, onCompleteCallback);
  }

  protected String getMessage(String responsePayload) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(responsePayload, Response.class)
        .getChoices()
        .get(0)
        .getDelta()
        .getContent();
  }
}
