package ee.carlrobert.codegpt.client.official;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

public abstract class CompletionClientEventListener extends EventSourceListener {

  private static final String DEFAULT_ERROR_MSG = "Something went wrong. Please try again later.";

  private final OkHttpClient client;
  private final Consumer<String> onMessageReceived;
  private final Consumer<String> onComplete;
  private final Consumer<String> onFailure;
  private final StringBuilder messageBuilder = new StringBuilder();

  public CompletionClientEventListener(
      OkHttpClient client,
      Consumer<String> onMessageReceived,
      Consumer<String> onComplete,
      Consumer<String> onFailure) {
    this.client = client;
    this.onMessageReceived = onMessageReceived;
    this.onComplete = onComplete;
    this.onFailure = onFailure;
  }

  protected abstract String getMessage(String data) throws JsonProcessingException;

  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
  }

  public void onClosed(@NotNull EventSource eventSource) {
  }

  public void onEvent(
      @NotNull EventSource eventSource,
      @Nullable String id,
      @Nullable String type,
      @NotNull String data) {
    try {
      if ("[DONE]".equals(data)) {
        onComplete.accept(messageBuilder.toString());
        return;
      }

      var message = getMessage(data);
      if (message != null) {
        messageBuilder.append(message);
        onMessageReceived.accept(message);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize payload.", e);
    }
  }

  public void onFailure(
      @NotNull EventSource eventSource,
      @Nullable Throwable ex,
      @Nullable Response response) {
    if (isRequestCancelled()) {
      onComplete.accept(messageBuilder.toString());
      return;
    }

    try {
      if (response == null) {
        onFailure.accept(DEFAULT_ERROR_MSG);
        return;
      }

      var body = response.body();
      if (body != null) {
        var error = new ObjectMapper().readValue(body.string(), ApiResponseError.class);
        onFailure.accept(error.getError().getMessage());
      }
    } catch (IOException e) {
      onFailure.accept(DEFAULT_ERROR_MSG);
    }
  }

  private boolean isRequestCancelled() {
    return client.dispatcher().runningCalls().stream().anyMatch(Call::isCanceled);
  }
}
