package ee.carlrobert.codegpt.client.unofficial;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.client.unofficial.response.ApiResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

public class UnofficialClientEventListener extends EventSourceListener {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String prompt;
  private final Consumer<String> onMessageReceived;
  private final Consumer<@NotNull ApiResponse> onComplete;
  private final Consumer<String> onFailure;
  private ApiResponse lastReceivedResponse;
  private boolean eventReceived = false;
  private final OkHttpClient client;

  public UnofficialClientEventListener(
      OkHttpClient client,
      String prompt,
      Consumer<String> onMessageReceived,
      Consumer<ApiResponse> onComplete,
      Consumer<String> onFailure) {
    this.client = client;
    this.prompt = prompt;
    this.onMessageReceived = onMessageReceived;
    this.onComplete = onComplete;
    this.onFailure = onFailure;
  }

  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
  }

  public void onClosed(@NotNull EventSource eventSource) {
    if (!eventReceived) {
      var clientInstance = UnofficialChatGPTClient.getInstance();
      try {
        var response = clientInstance.buildClient()
            .newCall(clientInstance.buildHttpRequest(prompt))
            .execute();
        tryExtractingErrorResponse(response);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void onEvent(
      @NotNull EventSource eventSource,
      @Nullable String id,
      @Nullable String type,
      @NotNull String data) {
    eventReceived = true;

    if ("[DONE]".equals(data)) {
      onComplete.accept(lastReceivedResponse);
      return;
    }

    if (!"ping".equals(type) && isValidJson(data)) {
      try {
        var response = objectMapper.readValue(data, ApiResponse.class);
        var author = response.getMessage().getAuthor();
        if (author != null && "assistant".equals(author.getRole())) {
          var message = response.getFullMessage();
          if (lastReceivedResponse != null) {
            message = message.replace(lastReceivedResponse.getFullMessage(), "");
          }
          lastReceivedResponse = response;
          onMessageReceived.accept(message);
        }
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to deserialize the payload", e);
      }
    }
  }

  public void onFailure(
      @NotNull EventSource eventSource,
      @Nullable Throwable ex,
      @Nullable Response response) {
    if (isRequestNotCancelled() && response != null) {
      try {
        tryExtractingErrorResponse(response);
      } catch (Exception e) {
        onFailure.accept("Something went wrong. Please try again later.");
      }
    } else {
      onComplete.accept(lastReceivedResponse);
    }
  }

  private boolean isRequestNotCancelled() {
    return client.dispatcher().runningCalls().stream().noneMatch(Call::isCanceled);
  }

  private void tryExtractingErrorResponse(Response response) {
    try (response) {
      var responseBody = response.body();
      if (responseBody != null) {
        tryExtractingErrorMessage(responseBody.string()).ifPresent(onFailure);
        responseBody.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<String> tryExtractingErrorMessage(String jsonPayload) {
    try {
      var error = objectMapper.readValue(jsonPayload, Map.class);
      var detail = error.get("detail");
      if (detail instanceof String) {
        return Optional.of((String) detail);
      } else if (detail instanceof Map) {
        return Optional.of((String) ((Map<?, ?>) detail).get("message"));
      } else {
        return Optional.empty();
      }
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }

  private boolean isValidJson(String json) {
    ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    try {
      mapper.readTree(json);
    } catch (JacksonException e) {
      return false;
    }
    return true;
  }
}
