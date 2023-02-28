package ee.carlrobert.chatgpt.client.gpt;

import static java.lang.String.format;

import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.BaseModel;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GPTClient extends Client {

  private static final List<Map.Entry<String, String>> queries = new ArrayList<>();
  private static GPTClient instance;

  private GPTClient() {
  }

  public static GPTClient getInstance() {
    if (instance == null) {
      instance = new GPTClient();
    }
    return instance;
  }

  public void clearPreviousSession() {
    queries.clear();
  }

  protected ApiRequestDetails getRequestDetails(String prompt) {
    return new ApiRequestDetails(
        format("https://api.openai.com/v1/engines/%s/completions", SettingsState.getInstance().baseModel.getModel()),
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

  protected BodySubscriber<Void> subscribe(
      ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      Runnable onComplete) {
    if (responseInfo.statusCode() == 200) {
      return new GPTBodySubscriber(
          onMessageReceived,
          finalMsg -> {
            queries.add(Map.entry(super.userPrompt, finalMsg));
            onComplete.run();
          });
    } else {
      handleError(responseInfo, onMessageReceived, onComplete);
      return null;
    }
  }

  private void handleError(
      ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      Runnable onComplete) {
    try {
      if (responseInfo.statusCode() == 401) {
        onMessageReceived.accept("Incorrect API key provided.\n" +
            "You can find your API key at https://platform.openai.com/account/api-keys.");
        throw new IllegalArgumentException();
      } else if (responseInfo.statusCode() == 429) {
        onMessageReceived.accept("You exceeded your current quota, please check your plan and billing details.");
        throw new RuntimeException("Insufficient quota");
      } else {
        onMessageReceived.accept("Something went wrong. Please try again later.");
        throw new RuntimeException();
      }
    } finally {
      onComplete.run();
    }
  }

  private StringBuilder getBasePrompt() {
    var isDavinciModel = SettingsState.getInstance().baseModel == BaseModel.DAVINCI;
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
    queries.forEach(query ->
        basePrompt.append("Human: ")
            .append(query.getKey())
            .append("\n")
            .append("AI: ")
            .append(query.getValue())
            .append("\n"));
    basePrompt.append("Human: ")
        .append(prompt)
        .append("\n")
        .append("AI: ")
        .append("\n");
    return basePrompt.toString();
  }
}
