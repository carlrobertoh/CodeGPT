package ee.carlrobert.codegpt.ide.toolwindow;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.conversations.message.Message;
import ee.carlrobert.codegpt.ide.toolwindow.chat.ChatGptToolWindow;
import ee.carlrobert.codegpt.ide.toolwindow.components.SyntaxTextArea;
import java.util.List;
import javax.swing.SwingWorker;
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

  public void startRequest(String prompt, SyntaxTextArea textArea, Project project) {
    var client = new ClientFactory().getClient();
    chatToolWindow.displayGenerateButton(client::cancelRequest);

    var conversationMessage = new Message(prompt);
    new SwingWorker<Void, String>() {
      protected Void doInBackground() {
        client.getCompletionsAsync(
            prompt,
            this::publish,
            (completedConversation) -> {
              ConversationsState.getInstance().saveConversation(completedConversation);
              chatToolWindow.stopGenerating(prompt, textArea, project);
            },
            (errorMessage) -> {
              var currentConversation = ConversationsState.getCurrentConversation();
              if (currentConversation != null) {
                conversationMessage.setResponse(errorMessage);
                currentConversation.addMessage(conversationMessage);
                ConversationsState.getInstance().saveConversation(currentConversation);
              }
              textArea.append(errorMessage);
              chatToolWindow.stopGenerating(prompt, textArea, project);
            });
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
