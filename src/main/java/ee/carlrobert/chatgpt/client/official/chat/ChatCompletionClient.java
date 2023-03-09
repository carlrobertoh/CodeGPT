package ee.carlrobert.chatgpt.client.official.chat;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.client.ClientCode;
import ee.carlrobert.chatgpt.client.official.chat.request.ApiRequest;
import ee.carlrobert.chatgpt.client.official.chat.request.ApiRequestMessage;
import ee.carlrobert.chatgpt.ide.conversations.Conversation;
import ee.carlrobert.chatgpt.ide.conversations.message.Message;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;
import okhttp3.sse.EventSourceListener;

public class ChatCompletionClient extends Client {

  private static ChatCompletionClient instance;

  private ChatCompletionClient() {
    super(ClientCode.CHAT_COMPLETIONS);
  }

  public static ChatCompletionClient getInstance() {
    if (instance == null) {
      instance = new ChatCompletionClient();
    }
    return instance;
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    var messages = new ArrayList<ApiRequestMessage>();
    messages.add(new ApiRequestMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."));
    conversation.getMessages().forEach(message -> {
      messages.add(new ApiRequestMessage("user", message.getPrompt()));
      messages.add(new ApiRequestMessage("assistant", message.getResponse()));
    });
    messages.add(new ApiRequestMessage("user", prompt));

    return new ApiRequestDetails(
        "https://api.openai.com/v1/chat/completions",
        new ApiRequest(
            SettingsState.getInstance().chatCompletionBaseModel.getCode(),
            true,
            messages
        ),
        SettingsState.getInstance().apiKey);
  }

  protected EventSourceListener getEventSourceListener(
      Consumer<String> onMessageReceived,
      Consumer<Conversation> onComplete,
      Consumer<String> onFailure) {
    return new ChatCompletionClientEventListener(client, onMessageReceived, finalMessage -> {
      var message = new Message();
      message.setPrompt(prompt);
      message.setResponse(finalMessage);
      conversation.setUpdatedOn(LocalDateTime.now());
      conversation.addMessage(message);
      onComplete.accept(conversation);
    }, onFailure);
  }
}
