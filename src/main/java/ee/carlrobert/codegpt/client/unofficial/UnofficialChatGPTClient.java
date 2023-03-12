package ee.carlrobert.codegpt.client.unofficial;

import ee.carlrobert.codegpt.client.ApiRequestDetails;
import ee.carlrobert.codegpt.client.Client;
import ee.carlrobert.codegpt.client.ClientCode;
import ee.carlrobert.codegpt.ide.conversations.Conversation;
import ee.carlrobert.codegpt.ide.conversations.message.Message;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import okhttp3.sse.EventSourceListener;

public class UnofficialChatGPTClient extends Client {

  private static UnofficialChatGPTClient instance;

  private UnofficialChatGPTClient() {
    super(ClientCode.UNOFFICIAL_CHATGPT);
  }

  public static UnofficialChatGPTClient getInstance() {
    if (instance == null) {
      instance = new UnofficialChatGPTClient();
    }
    return instance;
  }

  protected EventSourceListener getEventSourceListener(
      Consumer<String> onMessageReceived,
      Consumer<Conversation> onComplete,
      Consumer<String> onFailure) {
    return new UnofficialClientEventListener(client, prompt, onMessageReceived, (response) -> {
      var message = new Message(prompt);
      message.setResponse(response.getFullMessage());

      conversation.setUnofficialClientConversationId(response.getConversationId());
      conversation.setParentMessageId(response.getMessage().getId());
      conversation.setUpdatedOn(LocalDateTime.now());
      conversation.addMessage(message);
      onComplete.accept(conversation);
    }, onFailure);
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

    var conversationId = conversation.getUnofficialClientConversationId();
    var parentMessageId = conversation.getParentMessageId();
    if (conversationId != null && !conversationId.isEmpty() &&
        parentMessageId != null && !parentMessageId.isEmpty()) {
      payload.put("conversation_id", conversationId);
      payload.put("parent_message_id", parentMessageId);
    } else {
      payload.put("parent_message_id", UUID.randomUUID().toString());
    }

    return new ApiRequestDetails(settings.reverseProxyUrl, payload, settings.accessToken);
  }
}
