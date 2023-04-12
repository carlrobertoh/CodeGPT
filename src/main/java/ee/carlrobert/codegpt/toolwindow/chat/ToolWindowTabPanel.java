package ee.carlrobert.codegpt.toolwindow.chat;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import javax.swing.JPanel;

public interface ToolWindowTabPanel {

  JPanel getContent();

  Conversation getConversation();

  void displayLandingView();

  void displayConversation(Conversation conversation);

  void startNewConversation(Message message);

  void startConversation(Message message, boolean isRetry);
}
