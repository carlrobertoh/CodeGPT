package ee.carlrobert.chatgpt.client.official.text;

import static java.lang.String.format;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.BaseModel;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.client.ClientCode;
import ee.carlrobert.chatgpt.ide.conversations.Conversation;
import ee.carlrobert.chatgpt.ide.conversations.message.Message;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.sse.EventSourceListener;

public class TextCompletionClient extends Client {

  private static TextCompletionClient instance;

  private TextCompletionClient() {
    super(ClientCode.TEXT_COMPLETIONS);
  }

  public static TextCompletionClient getInstance() {
    if (instance == null) {
      instance = new TextCompletionClient();
    }
    return instance;
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    return new ApiRequestDetails(
        format("https://api.openai.com/v1/engines/%s/completions",
            SettingsState.getInstance().textCompletionBaseModel.getCode()),
        Map.of(
            "stop", List.of(" Human:", " AI:"),
            "prompt", buildPrompt(prompt),
            "max_tokens", 1000,
            "temperature", 0.9,
            "best_of", 1,
            "frequency_penalty", 0,
            "presence_penalty", 0.6,
            "top_p", 1,
            "stream", true
        ),
        SettingsState.getInstance().apiKey);
  }

  protected EventSourceListener getEventSourceListener(
      Consumer<String> onMessageReceived,
      Consumer<Conversation> onComplete,
      Consumer<String> onFailure) {
    return new TextCompletionClientEventListener(client, onMessageReceived, (finalMessage) -> {
      var message = new Message();
      message.setPrompt(prompt);
      message.setResponse(finalMessage);
      conversation.setUpdatedOn(LocalDateTime.now());
      conversation.addMessage(message);
      onComplete.accept(conversation);
    }, onFailure);
  }

  private StringBuilder getBasePrompt() {
    var isDavinciModel = SettingsState.getInstance().textCompletionBaseModel == BaseModel.DAVINCI;
    if (isDavinciModel) {
      return new StringBuilder(
          "You are ChatGPT, a large language model trained by OpenAI.\n" +
              "Answer in a markdown language, code blocks should contain language whenever possible.\n");
    }
    return new StringBuilder(
        "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\n");
  }

  private String buildPrompt(String prompt) {
    var basePrompt = getBasePrompt();
    conversation.getMessages().forEach(message ->
        basePrompt.append("Human: ")
            .append(message.getPrompt())
            .append("\n")
            .append("AI: ")
            .append(message.getResponse())
            .append("\n"));
    basePrompt.append("Human: ")
        .append(prompt)
        .append("\n")
        .append("AI: ")
        .append("\n");
    return basePrompt.toString();
  }
}
