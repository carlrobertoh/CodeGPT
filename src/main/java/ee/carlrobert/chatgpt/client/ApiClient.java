package ee.carlrobert.chatgpt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.settings.SettingsState;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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

  public void getCompletionsAsync(String prompt, Consumer<String> onMessage) {
    try {
      var query = new StringBuilder(
          "You are ChatGPT, a large language model trained by OpenAI.\n");
      for (var entry : queries) {
        query.append("User:\n")
            .append(entry.getKey())
            .append("<|im_end|>\n")
            .append("\n")
            .append("ChatGPT:\n")
            .append(entry.getValue())
            .append("<|im_end|>\n")
            .append("\n");
      }
      query.append("User:\n")
          .append(prompt)
          .append("<|im_end|>\n")
          .append("\n")
          .append("ChatGPT:\n");

      var req = HttpRequest.newBuilder()
          .uri(URI.create("https://api.openai.com/v1/completions"))
          .header("Accept", "text/event-stream")
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + SettingsState.getInstance().secretKey)
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(Map.of(
                  "model", "text-davinci-003",
                  "stop", List.of("<|im_end|>"),
                  "prompt", query.toString(),
                  "max_tokens", 400,
                  "temperature", 1.0,
                  "stream", true
              ))))
          .build();

      this.client.sendAsync(req, respInfo ->
      {
        if (respInfo.statusCode() == 200) {
          return new Subscriber((messageData ->
              onMessage.accept(messageData.getChoices().get(0).getText())),
              (finalMsg) -> queries.add(Map.entry(prompt, finalMsg)));
        } else if (respInfo.statusCode() == 401) {
          onMessage.accept("Incorrect API key provided.\n" +
              "You can find your API key at https://platform.openai.com/account/api-keys.");
          throw new IllegalArgumentException();
        } else {
          onMessage.accept("Something went wrong. Please try again later.");
          clearQueries();
          throw new RuntimeException();
        }
      });
    } catch (JsonProcessingException e) {
      onMessage.accept("Something went wrong. Please try again later.");
      throw new RuntimeException(e);
    }
  }

  public static ApiClient getInstance() {
    if (instance == null) {
      instance = new ApiClient();
    }
    return instance;
  }

  public void clearQueries() {
    queries.clear();
  }
}
