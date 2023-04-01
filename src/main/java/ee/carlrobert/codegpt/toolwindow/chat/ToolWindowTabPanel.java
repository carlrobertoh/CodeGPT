package ee.carlrobert.codegpt.toolwindow.chat;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import javax.swing.JPanel;

public interface ToolWindowTabPanel {

  JPanel getContent();

  Conversation getConversation();

  void displayLandingView();

  void displayConversation(Conversation conversation);

  void startNewConversation(String prompt);

  void startConversation(String prompt, boolean isRetry);

}
