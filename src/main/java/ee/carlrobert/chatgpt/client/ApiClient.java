package ee.carlrobert.chatgpt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ApiClient {

  private static final List<Map.Entry<String, String>> queries = new ArrayList<>(); // TODO
  private static ApiClient instance;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

  private ApiClient() {
  }

  public static ApiClient getInstance() {
    if (instance == null) {
      instance = new ApiClient();
    }
    return instance;
  }

  public void getCompletionsAsync(String userPrompt, Consumer<String> onMessage) {
    var prompt = buildCompletePrompt(userPrompt);
    this.client.sendAsync(buildHttpRequest(prompt), respInfo -> subscribe(respInfo, userPrompt, onMessage));
  }

  public void clearQueries() {
    queries.clear();
  }

  private String buildCompletePrompt(String prompt) {
    var basePrompt = new StringBuilder("You are ChatGPT, a large language model trained by OpenAI.\n");
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

  private HttpRequest buildHttpRequest(String prompt) {
    try {
      return HttpRequest.newBuilder()
          .uri(URI.create("https://api.openai.com/v1/completions"))
          .header("Accept", "text/event-stream")
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + SettingsState.getInstance().secretKey)
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(Map.of(
                  "model", "text-davinci-003",
                  "stop", List.of("<|im_end|>"),
                  "prompt", prompt,
                  "max_tokens", 1000,
                  "temperature", 1.0,
                  "stream", true
              ))))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize request payload", e);
    }
  }

  private BodySubscriber<Void> subscribe(ResponseInfo responseInfo, String userPrompt, Consumer<String> onMessage) {
    if (responseInfo.statusCode() == 200) {
      return new Subscriber((messageData ->
          onMessage.accept(messageData.getChoices().get(0).getText())),
          (finalMsg) -> queries.add(Map.entry(userPrompt, finalMsg)));
    } else if (responseInfo.statusCode() == 401) {
      onMessage.accept("Incorrect API key provided.\n" +
          "You can find your API key at https://platform.openai.com/account/api-keys.");
      throw new IllegalArgumentException();
    } else if (responseInfo.statusCode() == 429) {
      onMessage.accept("You exceeded your current quota, please check your plan and billing details.");
      throw new RuntimeException("Insufficient quota");
    } else {
      onMessage.accept("Something went wrong. Please try again later.");
      clearQueries();
      throw new RuntimeException();
    }
  }
}
