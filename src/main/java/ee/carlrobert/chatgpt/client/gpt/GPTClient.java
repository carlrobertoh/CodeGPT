package ee.carlrobert.chatgpt.client.gpt;

import ee.carlrobert.chatgpt.EmptyCallback;
import ee.carlrobert.chatgpt.client.ApiRequestDetails;
import ee.carlrobert.chatgpt.client.Client;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscriber;
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
        "https://api.openai.com/v1/completions",
        Map.of(
            "model", "text-davinci-003",
            "stop", List.of("<|im_end|>"),
            "prompt", buildPrompt(prompt),
            "max_tokens", 1000,
            "temperature", 1.0,
            "stream", true
        ),
        SettingsState.getInstance().apiKey);
  }

  protected BodySubscriber<Void> subscribe(
      HttpResponse.ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      EmptyCallback onComplete) {
    if (responseInfo.statusCode() == 200) {
      return new GPTBodySubscriber(
          response -> onMessageReceived.accept(response.getChoices().get(0).getText()),
          finalMsg -> {
            queries.add(Map.entry(super.userPrompt, finalMsg));
            onComplete.call();
          });
    } else if (responseInfo.statusCode() == 401) {
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
  }

  private String buildPrompt(String prompt) {
    var basePrompt = new StringBuilder("""
        You are ChatGPT, a large language model trained by OpenAI.
        One of your main goals is code generation but not only.
        Answer in a markdown language. Markdown code blocks should contain language whenever possible.
        """);
    queries.forEach(query ->
        basePrompt.append("User:\n")
            .append(query.getKey())
            .append("<|im_end|>\n")
            .append("\n")
            .append("ChatGPT:\n")
            .append(query.getValue())
            .append("<|im_end|>\n")
            .append("\n"));
    basePrompt.append("User:\n")
        .append(prompt)
        .append("<|im_end|>\n")
        .append("\n")
        .append("ChatGPT:\n");
    return basePrompt.toString();
  }
}
