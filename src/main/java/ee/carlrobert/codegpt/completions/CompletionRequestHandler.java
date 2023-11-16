package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouCompletionEventListener;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

public class CompletionRequestHandler {

  private static final Logger LOG = Logger.getInstance(CompletionRequestHandler.class);

  private final StringBuilder messageBuilder = new StringBuilder();
  private final boolean useContextualSearch;
  private final CompletionResponseEventListener completionResponseEventListener;
  private SwingWorker<Void, String> swingWorker;
  private EventSource eventSource;

  public CompletionRequestHandler(
      boolean useContextualSearch,
      CompletionResponseEventListener completionResponseEventListener) {
    this.useContextualSearch = useContextualSearch;
    this.completionResponseEventListener = completionResponseEventListener;
  }

  public void call(Conversation conversation, Message message, boolean retry) {
    swingWorker = new CompletionRequestWorker(conversation, message, retry);
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
      boolean retry,
      CompletionEventListener eventListener) {
    try {
      return CompletionRequestService.getInstance()
          .getChatCompletionAsync(conversation, message, retry, useContextualSearch, eventListener);
    } catch (Throwable ex) {
      var errorMessage = "Something went wrong";
      if (ex instanceof TotalUsageExceededException) {
        errorMessage =
            "The length of the context exceeds the maximum limit that the model can handle. "
                + "Try reducing the input message or maximum completion token size.";
      }
      completionResponseEventListener.handleError(new ErrorDetails(errorMessage), ex);
      throw ex;
    }
  }

  private class CompletionRequestWorker extends SwingWorker<Void, String> {

    private final Conversation conversation;
    private final Message message;
    private final boolean retry;

    public CompletionRequestWorker(Conversation conversation, Message message, boolean retry) {
      this.conversation = conversation;
      this.message = message;
      this.retry = retry;
    }

    protected Void doInBackground() {
      var settings = SettingsState.getInstance();
      try {
        eventSource = startCall(
            conversation,
            message,
            retry,
            new YouRequestCompletionEventListener());
      } catch (TotalUsageExceededException e) {
        completionResponseEventListener.handleTokensExceeded(conversation, message);
      } finally {
        sendInfo(settings);
      }
      return null;
    }

    protected void process(List<String> chunks) {
      message.setResponse(messageBuilder.toString());
      for (String text : chunks) {
        messageBuilder.append(text);
        completionResponseEventListener.handleMessage(text);
      }
    }

    class YouRequestCompletionEventListener implements YouCompletionEventListener {

      @Override
      public void onSerpResults(List<YouSerpResult> results) {
        completionResponseEventListener.handleSerpResults(results, message);
      }

      @Override
      public void onMessage(String message) {
        publish(message);
      }

      @Override
      public void onComplete(StringBuilder messageBuilder) {
        completionResponseEventListener.handleCompleted(
            messageBuilder.toString(),
            message,
            conversation,
            retry);
      }

      @Override
      public void onError(ErrorDetails error, Throwable ex) {
        try {
          completionResponseEventListener.handleError(error, ex);
        } finally {
          sendError(error, ex);
        }
      }
    }

    private void sendInfo(SettingsState settings) {
      TelemetryAction.COMPLETION.createActionMessage()
          .property("conversationId", conversation.getId().toString())
          .property("model", conversation.getModel())
          .property("service", settings.getSelectedService().getCode().toLowerCase())
          .send();
    }

    private void sendError(ErrorDetails error, Throwable ex) {
      var telemetryMessage = TelemetryAction.COMPLETION_ERROR.createActionMessage();
      if ("insufficient_quota".equals(error.getCode())) {
        telemetryMessage
            .property("type", "USER")
            .property("code", "INSUFFICIENT_QUOTA");
      } else {
        telemetryMessage
            .property("conversationId", conversation.getId().toString())
            .property("model", conversation.getModel())
            .error(new RuntimeException(error.toString(), ex));
      }
      telemetryMessage.send();
    }
  }
}
