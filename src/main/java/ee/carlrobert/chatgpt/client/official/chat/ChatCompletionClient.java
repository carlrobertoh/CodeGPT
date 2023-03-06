package ee.carlrobert.chatgpt.client.official.chat;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.client.official.chat.request.ApiRequest;
import ee.carlrobert.chatgpt.client.official.chat.request.ApiRequestMessage;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.sse.EventSourceListener;

public class ChatCompletionClient extends Client {

  private static final List<Map.Entry<String, String>> queries = new ArrayList<>();
  private static ChatCompletionClient instance;

  private ChatCompletionClient() {
  }

  public static ChatCompletionClient getInstance() {
    if (instance == null) {
      instance = new ChatCompletionClient();
    }
    return instance;
  }

  public void clearPreviousSession() {
    queries.clear();
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    var messages = new ArrayList<ApiRequestMessage>();
    messages.add(new ApiRequestMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."));
    queries.forEach(query -> {
      messages.add(new ApiRequestMessage("user", query.getKey()));
      messages.add(new ApiRequestMessage("assistant", query.getValue()));
    });
    messages.add(new ApiRequestMessage("user", prompt));

    return new ApiRequestDetails(
        "https://api.openai.com/v1/chat/completions",
        new ApiRequest(
            SettingsState.getInstance().chatCompletionBaseModel.getModel(),
            true,
            messages
        ),
        SettingsState.getInstance().apiKey);
  }

  protected EventSourceListener getEventSourceListener(Consumer<String> onMessageReceived, Runnable onComplete) {
    return new ChatCompletionClientEventListener(onMessageReceived, finalMessage -> {
      queries.add(Map.entry(prompt, finalMessage));
      onComplete.run();
    });
  }
}
