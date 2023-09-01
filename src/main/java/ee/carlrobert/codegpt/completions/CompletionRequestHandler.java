package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.CustomSettingsState;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionRequestHandler {

  private final StringBuilder messageBuilder = new StringBuilder();
  private SwingWorker<Void, String> swingWorker;
  private EventSource eventSource;
  private @Nullable Consumer<String> messageListener;
  private @Nullable BiConsumer<ErrorDetails, Throwable> errorListener;
  private @Nullable Consumer<String> completedListener;
  private @Nullable Runnable tokensExceededListener;
  private boolean useContextualSearch;

  public CompletionRequestHandler withContextualSearch(boolean useContextualSearch) {
    this.useContextualSearch = useContextualSearch;
    return this;
  }

  public void addMessageListener(Consumer<String> messageListener) {
    this.messageListener = messageListener;
  }

  public void addErrorListener(BiConsumer<ErrorDetails, Throwable> errorListener) {
    this.errorListener = errorListener;
  }

  public void addRequestCompletedListener(Consumer<String> completedListener) {
    this.completedListener = completedListener;
  }

  public void addTokensExceededListener(Runnable tokensExceededListener) {
    this.tokensExceededListener = tokensExceededListener;
  }

  public void call(Conversation conversation, Message message, boolean isRetry) {
    swingWorker = new CompletionRequestWorker(conversation, message, isRetry);
    swingWorker.execute();
  }

  public void cancel() {
    if (eventSource != null) {
      eventSource.cancel();
    }
    swingWorker.cancel(true);
  }

  private EventSource startCall(
      @NotNull Conversation conversation,
      @NotNull Message message,
      boolean isRetry,
      CompletionEventListener eventListener) {
    var modelSettings = ModelSettingsState.getInstance();
    var customServiceSettings = CustomSettingsState.getInstance();
    var requestProvider = new CompletionRequestProvider(conversation);

    try {
      if (SettingsState.getInstance().isUseCustomService()) {
        var requestBuilder = new Request.Builder();
        requestBuilder.url(customServiceSettings.getUrl());
        requestBuilder.addHeader("Accept", "text/event-stream");
        for (var entry : customServiceSettings.getHeaders().entrySet()) {
          requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        if ("POST".equals(customServiceSettings.getHttpMethod())) {
          var body = customServiceSettings.getBodyJson().replace("{{PROMPT}}", message.getPrompt());
          requestBuilder.post(RequestBody.create(body.getBytes()));
        } else {
          requestBuilder.get();
        }

        CompletionClientProvider.getCustomChatCompletionClient().newCall(requestBuilder.build());

        return EventSources.createFactory(CompletionClientProvider.getCustomChatCompletionClient())
            .newEventSource(requestBuilder.build(), new EventSourceListener() {
              @Override
              public void onClosed(@NotNull EventSource eventSource) {
                super.onClosed(eventSource);
              }

              @Override
              public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                super.onEvent(eventSource, id, type, data);
              }

              @Override
              public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                if (errorListener != null) {
                  errorListener.accept(new ErrorDetails("Something went wrong. " + response), t);
                }
              }

              @Override
              public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                super.onOpen(eventSource, response);
              }
            });
      }

      if (modelSettings.isUseChatCompletion()) {
        return CompletionClientProvider.getChatCompletionClient().stream(
            requestProvider.buildChatCompletionRequest(modelSettings.getChatCompletionModel(), message, isRetry, useContextualSearch), eventListener);
      }
      return CompletionClientProvider.getTextCompletionClient().stream(
          requestProvider.buildTextCompletionRequest(modelSettings.getTextCompletionModel(), message, isRetry), eventListener);
    } catch (Throwable t) {
      if (errorListener != null) {
        errorListener.accept(new ErrorDetails("Something went wrong"), t);
      }
      throw t;
    }
  }

  private class CompletionRequestWorker extends SwingWorker<Void, String> {

    private final Conversation conversation;
    private final Message message;
    private final boolean isRetry;

    public CompletionRequestWorker(Conversation conversation, Message message, boolean isRetry) {
      this.conversation = conversation;
      this.message = message;
      this.isRetry = isRetry;
    }

    protected Void doInBackground() {
      try {
        eventSource = startCall(conversation, message, isRetry, new CompletionEventListener() {
          @Override
          public void onMessage(String message) {
            publish(message);
          }

          @Override
          public void onComplete(StringBuilder messageBuilder) {
            if (completedListener != null) {
              completedListener.accept(messageBuilder.toString());
            }
          }

          @Override
          public void onError(ErrorDetails error, Throwable ex) {
            if (errorListener != null) {
              errorListener.accept(error, ex);
            }
          }
        });
      } catch (TotalUsageExceededException e) {
        if (tokensExceededListener != null) {
          tokensExceededListener.run();
        }
      }
      return null;
    }

    protected void process(List<String> chunks) {
      message.setResponse(messageBuilder.toString());
      for (String text : chunks) {
        messageBuilder.append(text);
        if (messageListener != null) {
          messageListener.accept(text);
        }
      }
    }
  }
}
