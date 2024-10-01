package ee.carlrobert.codegpt.completions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.events.CodeGPTEvent;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import okhttp3.sse.EventSource;

public class ChatCompletionEventListener implements CompletionEventListener<String> {

  private final CallParameters callParameters;
  private final CompletionResponseEventListener eventListener;
  private final StringBuilder messageBuilder = new StringBuilder();

  public ChatCompletionEventListener(
      CallParameters callParameters,
      CompletionResponseEventListener eventListener) {
    this.callParameters = callParameters;
    this.eventListener = eventListener;
  }

  @Override
  public void onEvent(String data) {
    try {
      var event = new ObjectMapper().readValue(data, CodeGPTEvent.class);
      eventListener.handleCodeGPTEvent(event);
    } catch (JsonProcessingException e) {
      // ignore
    }
  }

  @Override
  public void onMessage(String message, EventSource eventSource) {
    messageBuilder.append(message);
    callParameters.getMessage().setResponse(messageBuilder.toString());
    eventListener.handleMessage(message);
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    eventListener.handleCompleted(messageBuilder.toString(), callParameters);
  }

  @Override
  public void onCancelled(StringBuilder messageBuilder) {
    eventListener.handleCompleted(messageBuilder.toString(), callParameters);
  }

  @Override
  public void onError(ErrorDetails error, Throwable ex) {
    try {
      eventListener.handleError(error, ex);
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
