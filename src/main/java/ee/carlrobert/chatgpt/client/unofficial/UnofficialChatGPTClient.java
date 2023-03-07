package ee.carlrobert.chatgpt.client.unofficial;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.client.unofficial.response.ApiResponse;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import okhttp3.sse.EventSourceListener;

public class UnofficialChatGPTClient extends Client {

  private static UnofficialChatGPTClient instance;
  private static ApiResponse lastReceivedResponse;

  private UnofficialChatGPTClient() {
  }

  public static UnofficialChatGPTClient getInstance() {
    if (instance == null) {
      instance = new UnofficialChatGPTClient();
    }
    return instance;
  }

  public void clearPreviousSession() {
    lastReceivedResponse = null;
  }

  protected EventSourceListener getEventSourceListener(Consumer<String> onMessageReceived, Runnable onComplete) {
    return new UnofficialClientEventListener(client, prompt, onMessageReceived, (response) -> {
      if (response != null) {
        lastReceivedResponse = response;
      }
      onComplete.run();
    });
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

    return new ApiRequestDetails(settings.reverseProxyUrl, payload, settings.accessToken);
  }
}
