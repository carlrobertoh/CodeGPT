package ee.carlrobert.chatgpt.client.official;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

public abstract class CompletionClientEventListener extends EventSourceListener {

  private static final String DEFAULT_ERROR_MSG = "Something went wrong. Please try again later.";

  private final Consumer<String> onMessageReceived;
  private final Consumer<String> onComplete;
  private final StringBuilder messageBuilder = new StringBuilder();

  public CompletionClientEventListener(Consumer<String> onMessageReceived, Consumer<String> onComplete) {
    this.onMessageReceived = onMessageReceived;
    this.onComplete = onComplete;
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
      messageBuilder.append(message);
      onMessageReceived.accept(message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize payload.", e);
    }
  }

  public void onFailure(
      @NotNull EventSource eventSource,
      @Nullable Throwable ex,
      @Nullable Response response) {
    if (response == null) {
      onMessageReceived.accept(DEFAULT_ERROR_MSG);
      onComplete.accept(messageBuilder.toString());
      return;
    }

    try {
      var body = response.body();
      if (body != null) {
        var error = new ObjectMapper().readValue(body.string(), ApiResponseError.class);
        onMessageReceived.accept(error.getError().getMessage());
      }
    } catch (IOException e) {
      onMessageReceived.accept(DEFAULT_ERROR_MSG);
    } finally {
      onComplete.accept(messageBuilder.toString());
    }
  }
}
