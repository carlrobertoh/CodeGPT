package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import okhttp3.sse.EventSource;

public class ToolwindowChatCompletionRequestHandler {

  private final CompletionResponseEventListener completionResponseEventListener;
  private EventSource eventSource;

  public ToolwindowChatCompletionRequestHandler(
      CompletionResponseEventListener completionResponseEventListener) {
    this.completionResponseEventListener = completionResponseEventListener;
  }

  public void call(CallParameters callParameters) {
    try {
      eventSource = startCall(callParameters);
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

  private EventSource startCall(CallParameters callParameters) {
    try {
      var request = CompletionRequestFactory
          .getFactory(GeneralSettings.getSelectedService())
          .createChatRequest(new ChatCompletionRequestParameters(callParameters));
      return CompletionRequestService.getInstance().getChatCompletionAsync(
          request,
          new ChatCompletionEventListener(callParameters, completionResponseEventListener));
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

  private void sendInfo(CallParameters callParameters) {
    TelemetryAction.COMPLETION.createActionMessage()
        .property("conversationId", callParameters.getConversation().getId().toString())
        .property("model", callParameters.getConversation().getModel())
        .property("service", GeneralSettings.getSelectedService().getCode().toLowerCase())
        .send();
  }
}
