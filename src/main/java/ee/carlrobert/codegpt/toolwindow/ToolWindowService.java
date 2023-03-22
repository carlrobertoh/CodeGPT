package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.client.ClientRequestFactory;
import ee.carlrobert.codegpt.client.EventListener;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowPanel;
import ee.carlrobert.codegpt.toolwindow.components.SyntaxTextArea;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;

public class ToolWindowService {

  public void startRequest(
      String prompt,
      SyntaxTextArea textArea,
      Project project,
      boolean isRetry,
      ChatToolWindowPanel toolWindow,
      Conversation conversation) {
    var conversationMessage = new Message(prompt);

    new SwingWorker<Void, String>() {
      protected Void doInBackground() {
        var eventListener = new EventListener(
            conversationMessage,
            textArea::append,
            () -> toolWindow.stopGenerating(prompt, textArea, project),
            isRetry) {
          public void onMessage(String message) {
            publish(message);
          }
        };

        EventSource call;
        var settings = SettingsState.getInstance();
        var requestFactory = new ClientRequestFactory(prompt, conversation);
        if (settings.isChatCompletionOptionSelected) {
          call = ClientFactory.getChatCompletionClient().stream(
              requestFactory.buildChatCompletionRequest(settings), eventListener);
        } else {
          call = ClientFactory.getTextCompletionClient().stream(
              requestFactory.buildTextCompletionRequest(settings), eventListener);
        }
        toolWindow.displayGenerateButton(call::cancel);
        return null;
      }

      protected void process(List<String> chunks) {
        for (String text : chunks) {
          try {
            textArea.append(text);
            conversationMessage.setResponse(textArea.getText());
            toolWindow.scrollToBottom();
          } catch (Exception e) {
            textArea.append("Something went wrong. Please try again later.");
            throw new RuntimeException(e);
          }
        }
      }
    }.execute();
  }
}
