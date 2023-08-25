package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionRequestHandler {

  private final Project project;
  private final StringBuilder messageBuilder = new StringBuilder();
  private SwingWorker<Void, String> swingWorker;
  private EventSource eventSource;
  private @Nullable Consumer<String> messageListener;
  private @Nullable BiConsumer<ErrorDetails, Throwable> errorListener;
  private @Nullable Consumer<String> completedListener;
  private @Nullable Runnable tokensExceededListener;
  private boolean useContextualSearch;

  public CompletionRequestHandler(@NotNull Project project) {
    this.project = project;
  }

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
    swingWorker = new SwingWorker<>() {
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
    };
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
    var settings = SettingsState.getInstance();
    var requestProvider = new CompletionRequestProvider(project, conversation);

    if (settings.isChatCompletionOptionSelected) {
      return CompletionClientProvider.getChatCompletionClient(settings).stream(
          requestProvider.buildChatCompletionRequest(settings.getChatCompletionModel(), message, isRetry, useContextualSearch), eventListener);
    }
    return CompletionClientProvider.getTextCompletionClient(settings).stream(
        requestProvider.buildTextCompletionRequest(settings.getTextCompletionModel(), message, isRetry), eventListener);
  }
}
