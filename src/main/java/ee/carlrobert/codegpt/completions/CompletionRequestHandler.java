package ee.carlrobert.codegpt.completions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.events.CodeGPTEvent;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import okhttp3.sse.EventSource;

public class CompletionRequestHandler {

  private final StringBuilder messageBuilder = new StringBuilder();
  private final CompletionResponseEventListener completionResponseEventListener;
  private EventSource eventSource;

  public CompletionRequestHandler(CompletionResponseEventListener completionResponseEventListener) {
    this.completionResponseEventListener = completionResponseEventListener;
  }

  public void call(CallParameters callParameters) {
    try {
      eventSource = startCall(callParameters, new RequestCompletionEventListener(callParameters));
    } catch (TotalUsageExceededException e) {
      completionResponseEventListener.handleTokensExceeded(
          callParameters.getConversation(),
          callParameters.getMessage());
    } finally {
      sendInfo(callParameters);
    }
  }

  public void cancel() {
    if (eventSource != null) {
      eventSource.cancel();
    }
  }

  private EventSource startCall(
      CallParameters callParameters,
      CompletionEventListener<String> eventListener) {
    try {
      return CompletionRequestService.getInstance()
          .getChatCompletionAsync(callParameters, eventListener);
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

  class RequestCompletionEventListener implements CompletionEventListener<String> {

    private final CallParameters callParameters;

    public RequestCompletionEventListener(CallParameters callParameters) {
      this.callParameters = callParameters;
    }

    @Override
    public void onEvent(String data) {
      try {
        var event = new ObjectMapper().readValue(data, CodeGPTEvent.class);
        completionResponseEventListener.handleCodeGPTEvent(event);
      } catch (JsonProcessingException e) {
        // ignore
      }
    }

    @Override
    public void onMessage(String message, EventSource eventSource) {
      messageBuilder.append(message);
      callParameters.getMessage().setResponse(messageBuilder.toString());
      completionResponseEventListener.handleMessage(message);
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

  private void sendInfo(CallParameters callParameters) {
    TelemetryAction.COMPLETION.createActionMessage()
        .property("conversationId", callParameters.getConversation().getId().toString())
        .property("model", callParameters.getConversation().getModel())
        .property("service", GeneralSettings.getSelectedService().getCode().toLowerCase())
        .send();
  }
}
