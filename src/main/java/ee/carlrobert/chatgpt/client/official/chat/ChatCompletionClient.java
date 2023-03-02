package ee.carlrobert.chatgpt.client.official.chat;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.official.CompletionClient;
import ee.carlrobert.chatgpt.client.official.CompletionSubscriber;
import ee.carlrobert.chatgpt.client.official.chat.request.Request;
import ee.carlrobert.chatgpt.client.official.chat.request.RequestMessage;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ChatCompletionClient extends CompletionClient {

  private static ChatCompletionClient instance;

  private ChatCompletionClient() {
  }

  public static ChatCompletionClient getInstance() {
    if (instance == null) {
      instance = new ChatCompletionClient();
    }
    return instance;
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    var messages = new ArrayList<RequestMessage>();
    messages.add(new RequestMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."));
    queries.forEach(query -> {
      messages.add(new RequestMessage("user", query.getKey()));
      messages.add(new RequestMessage("assistant", query.getValue()));
    });
    messages.add(new RequestMessage("user", prompt));

    return new ApiRequestDetails(
        "https://api.openai.com/v1/chat/completions",
        new Request(
            SettingsState.getInstance().chatCompletionBaseModel.getModel(),
            true,
            messages
        ),
        SettingsState.getInstance().apiKey);
  }

  protected CompletionSubscriber createSubscriber(
      Consumer<String> responseConsumer,
      Consumer<String> onCompleteCallback) {
    return new ChatCompletionSubscriber(responseConsumer, onCompleteCallback);
  }
}
