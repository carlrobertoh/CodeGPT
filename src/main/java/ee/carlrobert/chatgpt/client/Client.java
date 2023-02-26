package ee.carlrobert.chatgpt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.EmptyCallback;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.function.Consumer;

public abstract class Client {

  private final HttpClient client =
      HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
  private final ObjectMapper objectMapper = new ObjectMapper();
  protected String userPrompt = "";

  protected abstract ApiRequestDetails getRequestDetails(String prompt);

  public abstract void clearPreviousSession();

  protected abstract BodySubscriber<Void> subscribe(
      ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      EmptyCallback onComplete);

  public void getCompletionsAsync(
      String prompt,
      Consumer<String> onMessageReceived,
      EmptyCallback onComplete) {
    this.userPrompt = prompt;
    this.client.sendAsync(
        buildHttpRequest(prompt),
        responseInfo -> subscribe(responseInfo, onMessageReceived, onComplete));
  }

  private HttpRequest buildHttpRequest(String prompt) {
    var requestDetails = getRequestDetails(prompt);
    try {
      return HttpRequest.newBuilder()
          .uri(URI.create(requestDetails.getUrl()))
          .header("Accept", "text/event-stream")
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + requestDetails.getToken())
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(requestDetails.getBody())))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize request payload", e);
    }
  }
}
