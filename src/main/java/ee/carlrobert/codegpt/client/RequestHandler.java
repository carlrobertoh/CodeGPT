package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;

public class RequestHandler implements ActionListener {

  private final Conversation conversation;
  private final StringBuilder messageBuilder = new StringBuilder();
  private SwingWorker<Void, String> swingWorker;
  private EventSource eventSource;

  public RequestHandler(Conversation conversation) {
    this.conversation = conversation;
  }

  public void call(Message message, boolean isRetry) {
    swingWorker = new SwingWorker<>() {
      protected Void doInBackground() {
        var eventListener = new EventListener(
            conversation,
            message,
            RequestHandler.this::handleComplete,
            RequestHandler.this::handleError,
            isRetry) {
          public void onMessage(String message) {
            publish(message);
          }
        };

        try {
          eventSource = startCall(message, eventListener);
        } catch (TotalUsageExceededException e) {
          handleTokensExceeded();
        }
        return null;
      }

      protected void process(List<String> chunks) {
        message.setResponse(messageBuilder.toString());
        for (String text : chunks) {
          messageBuilder.append(text);
          handleMessage(text, messageBuilder.toString());
        }
      }
    };
    swingWorker.execute();
  }

  public void cancel() {
    eventSource.cancel();
    swingWorker.cancel(true);
  }

  private EventSource startCall(Message message, EventListener eventListener) {
    var settings = SettingsState.getInstance();
    var requestProvider = new CompletionRequestProvider(message.getPrompt(), conversation);

    if (settings.isChatCompletionOptionSelected) {
      return ClientProvider.getChatCompletionClient(settings).stream(
          requestProvider.buildChatCompletionRequest(settings.getChatCompletionModel()),
          eventListener);
    }
    return ClientProvider.getTextCompletionClient(settings).stream(
        requestProvider.buildTextCompletionRequest(settings.getTextCompletionModel()),
        eventListener);
  }
}
