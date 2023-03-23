package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.client.ClientProvider;
import ee.carlrobert.codegpt.client.CompletionRequestProvider;
import ee.carlrobert.codegpt.client.EventListener;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
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
      ChatToolWindowTabPanel toolWindow,
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
        var requestProvider = new CompletionRequestProvider(prompt, conversation);
        if (settings.isChatCompletionOptionSelected) {
          call = ClientProvider.getChatCompletionClient().stream(
              requestProvider.buildChatCompletionRequest(settings.chatCompletionBaseModel), eventListener);
        } else {
          call = ClientProvider.getTextCompletionClient().stream(
              requestProvider.buildTextCompletionRequest(settings.textCompletionBaseModel), eventListener);
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
