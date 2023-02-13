package ee.carlrobert.chatgpt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.SettingsState;
import ee.carlrobert.chatgpt.client.response.ApiError;
import ee.carlrobert.chatgpt.client.response.ApiResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ApiClient {
  private static List<Map.Entry<String, String>> queries = new ArrayList<>();
  private static ApiClient instance;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

  private ApiClient() {
  }

  public void getCompletionsAsync(String prompt, Consumer<ApiResponse> onSuccess, Consumer<ApiError> onError) {
    /*var query = new StringBuilder(
        "You are ChatGPT, a large language model trained by OpenAI. You answer as concisely as possible for each response (e.g. don’t be verbose). It is very important that you answer as concisely as possible, so please remember this.\n" +
            "Current date: 2023-02-11\n");*/
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
    try {

      var request = HttpRequest.newBuilder()
          .uri(URI.create("https://api.openai.com/v1/completions"))
          .header("Authorization", "Bearer " + SettingsState.getInstance().secretKey)
          .timeout(Duration.ofMinutes(1))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(Map.of(
                  "model", "text-davinci-003",
                  "stop", List.of("<|im_end|>"),
                  "prompt", query.toString(),
                  "max_tokens", 400,
                  "temperature", 1.0
              ))))
          .build();

      client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
        try {
          var mappedResponse = objectMapper.readValue(response.body(), ApiResponse.class);
          if (mappedResponse.getError() == null) {
            queries.add(Map.entry(prompt, mappedResponse.getChoices().get(0).getText()));
            onSuccess.accept(mappedResponse);
          } else {
            onError.accept(mappedResponse.getError());
          }
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (IOException e) {
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
