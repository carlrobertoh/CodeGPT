package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouCompletionEventListener;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;

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

  public void call(CallParameters callParameters) {
    swingWorker = new CompletionRequestWorker(callParameters);
    swingWorker.execute();
  }

  public void cancel() {
    if (eventSource != null) {
      eventSource.cancel();
    }
    swingWorker.cancel(true);
  }

  private EventSource startCall(
      CallParameters callParameters,
      CompletionEventListener eventListener) {
    try {
      return CompletionRequestService.getInstance()
          .getChatCompletionAsync(callParameters, useContextualSearch, eventListener);
    } catch (Throwable ex) {
      handleCallException(ex);
      throw ex;
    }
  }

  private void handleCallException(Throwable ex) {
    var errorMessage = "Something went wrong";
    if (ex instanceof TotalUsageExceededException) {
      errorMessage =
          "The length of the context exceeds the maximum limit that the model can handle. "
              + "Try reducing the input message or maximum completion token size.";
    }
    completionResponseEventListener.handleError(new ErrorDetails(errorMessage), ex);
  }

  private class CompletionRequestWorker extends SwingWorker<Void, String> {

    private final CallParameters callParameters;

    public CompletionRequestWorker(CallParameters callParameters) {
      this.callParameters = callParameters;
    }

    protected Void doInBackground() {
      var settings = SettingsState.getInstance();
      try {
        eventSource = startCall(callParameters, new YouRequestCompletionEventListener());
      } catch (TotalUsageExceededException e) {
        completionResponseEventListener.handleTokensExceeded(
            callParameters.getConversation(),
            callParameters.getMessage());
      } finally {
        sendInfo(settings);
      }
      return null;
    }

    protected void process(List<String> chunks) {
      callParameters.getMessage().setResponse(messageBuilder.toString());
      for (String text : chunks) {
        messageBuilder.append(text);
        completionResponseEventListener.handleMessage(text);
      }
    }

    class YouRequestCompletionEventListener implements YouCompletionEventListener {

      @Override
      public void onSerpResults(List<YouSerpResult> results) {
        completionResponseEventListener.handleSerpResults(results, callParameters.getMessage());
      }

      @Override
      public void onMessage(String message) {
        publish(message);
      }

      @Override
      public void onComplete(StringBuilder messageBuilder) {
        completionResponseEventListener.handleCompleted(messageBuilder.toString(), callParameters);
      }

      @Override
      public void onCancelled(StringBuilder messageBuilder) {
        completionResponseEventListener.handleCompleted(messageBuilder.toString(), callParameters);
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
          .property("conversationId", callParameters.getConversation().getId().toString())
          .property("model", callParameters.getConversation().getModel())
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
            .property("conversationId", callParameters.getConversation().getId().toString())
            .property("model", callParameters.getConversation().getModel())
            .error(new RuntimeException(error.toString(), ex));
      }
      telemetryMessage.send();
    }
  }
}
