package ee.carlrobert.codegpt.toolwindow;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.client.ClientRequestFactory;
import ee.carlrobert.codegpt.client.EventListener;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatGptToolWindow;
import ee.carlrobert.codegpt.toolwindow.components.SyntaxTextArea;
import java.util.List;
import javax.swing.SwingWorker;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

public class ToolWindowService implements LafManagerListener {

  private ChatGptToolWindow chatToolWindow;

  @Override
  public void lookAndFeelChanged(@NotNull LafManager source) {
    chatToolWindow.changeStyle();
  }

  public void setChatToolWindow(ChatGptToolWindow chatToolWindow) {
    this.chatToolWindow = chatToolWindow;
  }

  public ChatGptToolWindow getChatToolWindow() {
    return chatToolWindow;
  }

  public void startRequest(String prompt, SyntaxTextArea textArea, Project project, boolean isRetry) {
    var conversationsState = ConversationsState.getInstance();
    var currentConversation = ConversationsState.getCurrentConversation();
    var conversation = currentConversation == null ? conversationsState.startConversation() : currentConversation;

    Message conversationMessage;
    if (isRetry) {
      var messages = conversation.getMessages();
      conversationMessage = messages.get(messages.size() - 1);
      conversationMessage.setResponse("");
    } else {
      conversationMessage = new Message(prompt);
    }

    new SwingWorker<Void, String>() {
      protected Void doInBackground() {
        var eventListener = new EventListener(
            conversationMessage,
            textArea::append,
            () -> chatToolWindow.stopGenerating(prompt, textArea, project)) {
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
        chatToolWindow.displayGenerateButton(call::cancel);
        return null;
      }

      protected void process(List<String> chunks) {
        for (String text : chunks) {
          try {
            textArea.append(text);
            conversationMessage.setResponse(textArea.getText());
            chatToolWindow.scrollToBottom();
          } catch (Exception e) {
            textArea.append("Something went wrong. Please try again later.");
            throw new RuntimeException(e);
          }
        }
      }
    }.execute();
  }
}
