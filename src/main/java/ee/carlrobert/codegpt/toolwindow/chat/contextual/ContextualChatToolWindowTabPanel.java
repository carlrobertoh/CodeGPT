package ee.carlrobert.codegpt.toolwindow.chat.contextual;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.BaseChatToolWindowTabPanel;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ContextualChatToolWindowTabPanel extends BaseChatToolWindowTabPanel {

  public ContextualChatToolWindowTabPanel(@NotNull Project project) {
    super(project, true);
    displayLandingView();
  }

  @Override
  protected JComponent getLandingView() {
    return new ContextualChatToolWindowLandingPanel(project, (prompt) -> {
      var message = new Message(prompt);
      if (conversation == null) {
        startNewConversation(message);
      } else {
        sendMessage(message);
      }
    });
  }

  @Override
  public void displayConversation(Conversation conversation) {
  }
}
