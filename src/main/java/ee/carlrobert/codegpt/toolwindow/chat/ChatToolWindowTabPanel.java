package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.Disposable;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensDetails;
import javax.swing.JComponent;
import javax.swing.JPanel;

public interface ChatToolWindowTabPanel extends Disposable {

  JComponent getContent();

  Conversation getConversation();

  TotalTokensDetails getTokenDetails();

  void displayLandingView();

  void sendMessage(Message message);

  void requestFocusForTextArea();
}
