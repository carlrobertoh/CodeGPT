package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouCompletionEventListener;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionRequestHandler {

  private final StringBuilder messageBuilder = new StringBuilder();
  private SwingWorker<Void, String> swingWorker;
  private EventSource eventSource;
  private @Nullable Consumer<String> messageListener;
  private @Nullable BiConsumer<ErrorDetails, Throwable> errorListener;
  private @Nullable Consumer<String> completedListener;
  private @Nullable Consumer<List<YouSerpResult>> serpResultsListener;
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

  public void addSerpResultsListener(Consumer<List<YouSerpResult>> serpResultsListener) {
    this.serpResultsListener = serpResultsListener;
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
    var settings = SettingsState.getInstance();
    var requestProvider = new CompletionRequestProvider(conversation);

    try {
      if (settings.isUseYouService()) {
        return CompletionClientProvider.getYouClient("", "")
            .getChatCompletion(requestProvider.buildYouCompletionRequest(message), eventListener);
      }

      var openAICompletionRequest = requestProvider.buildOpenAIChatCompletionRequest(
          ModelSettingsState.getInstance().getChatCompletionModel(), message, isRetry, useContextualSearch);
      if (settings.isUseAzureService()) {
        return CompletionClientProvider.getAzureClient().getChatCompletion(openAICompletionRequest, eventListener);
      }
      return CompletionClientProvider.getOpenAIClient().getChatCompletion(openAICompletionRequest, eventListener);
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
        eventSource = startCall(
            conversation,
            message,
            isRetry,
            SettingsState.getInstance().isUseYouService() ? getYouCompletionEventListener() : getCompletionEventListener());
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

    private CompletionEventListener getCompletionEventListener() {
      return new CompletionEventListener() {
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
      };
    }

    // TODO: Refactor
    private YouCompletionEventListener getYouCompletionEventListener() {
      return new YouCompletionEventListener() {
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

        @Override
        public void onSerpResults(List<YouSerpResult> results) {
          if (serpResultsListener != null) {
            serpResultsListener.accept(results);
          }
        }
      };
    }
  }
}
