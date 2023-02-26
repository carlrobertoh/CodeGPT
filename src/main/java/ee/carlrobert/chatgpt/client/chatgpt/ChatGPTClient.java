package ee.carlrobert.chatgpt.client.chatgpt;

import ee.carlrobert.chatgpt.EmptyCallback;
import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.client.chatgpt.response.ChatGPTResponse;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatGPTClient extends Client {

  private static ChatGPTClient instance;
  private static ChatGPTResponse lastReceivedResponse;


  private ChatGPTClient() {
  }

  public static ChatGPTClient getInstance() {
    if (instance == null) {
      instance = new ChatGPTClient();
    }
    return instance;
  }

  public void clearPreviousSession() {
    lastReceivedResponse = null;
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    var settings = SettingsState.getInstance();
    var payload = new HashMap<>(Map.of(
        "action", "next",
        "messages", List.of(Map.of(
            "id", UUID.randomUUID().toString(),
            "role", "user",
            "author", Map.of("role", "user"),
            "content", Map.of(
                "content_type", "text",
                "parts", List.of(prompt)
            )
        )),
        "model", "text-davinci-002-render-sha"
    ));

    if (lastReceivedResponse != null) {
      payload.put("conversation_id", lastReceivedResponse.getConversationId());
      payload.put("parent_message_id", lastReceivedResponse.getMessage().getId());
    } else {
      payload.put("parent_message_id", UUID.randomUUID().toString());
    }

    return new ApiRequestDetails(
        settings.reverseProxyUrl,
        payload,
        settings.accessToken);
  }

  protected HttpResponse.BodySubscriber<Void> subscribe(
      HttpResponse.ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      EmptyCallback onComplete) {
    if (responseInfo.statusCode() == 200) {
      return new ChatGPTBodySubscriber((
          response -> onMessageReceived.accept(String.join("", response.getMessage().getContent().getParts()))),
          response -> {
            lastReceivedResponse = response;
            onComplete.call();
          },
          error -> {
            if ("invalid_api_key".equals(error.getDetail().getCode())) {
              onMessageReceived.accept(error.getDetail().getMessage());
            }
          });
    } else {
      onMessageReceived.accept("Something went wrong. Please try again later.");
      throw new RuntimeException();
    }
  }
}
