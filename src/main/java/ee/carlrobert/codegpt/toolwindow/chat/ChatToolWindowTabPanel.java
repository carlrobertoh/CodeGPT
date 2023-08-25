package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.Disposable;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

public interface ChatToolWindowTabPanel extends Disposable {

  JPanel getContent();

  @Nullable Conversation getConversation();

  void setConversation(@Nullable Conversation conversation);

  void displayLandingView();

  void displayConversation(Conversation conversation);

  void startNewConversation(Message message);

  void sendMessage(Message message);
  void requestFocusForTextArea();
}
