package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;

public class RequestHandler implements ActionListener {

  private final Conversation conversation;
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
        eventSource = startCall(message, eventListener);
        return null;
      }

      protected void process(List<String> chunks) {
        for (String text : chunks) {
          handleMessage(text);
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
      return ClientProvider.getChatCompletionClient().stream(
          requestProvider.buildChatCompletionRequest(settings.chatCompletionBaseModel), eventListener);
    }
    return ClientProvider.getTextCompletionClient().stream(
        requestProvider.buildTextCompletionRequest(settings.textCompletionBaseModel), eventListener);
  }
}
