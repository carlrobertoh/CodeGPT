package ee.carlrobert.codegpt.toolwindow.chat.contextual;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.BaseChatToolWindowTabPanel;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ContextualChatToolWindowTabPanel extends BaseChatToolWindowTabPanel {

  public ContextualChatToolWindowTabPanel(
      @NotNull Project project,
      @NotNull Conversation conversation) {
    super(project, conversation, true);
    displayLandingView();
  }

  @Override
  protected JComponent getLandingView() {
    return new ContextualChatToolWindowLandingPanel(
        project,
        (prompt) -> sendMessage(new Message(prompt)));
  }
}
