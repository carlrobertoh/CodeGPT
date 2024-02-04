package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.lang.String.format;

import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.YouSettings;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.UserMessagePanel;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class StandardChatToolWindowTabPanel extends ChatToolWindowTabPanel {

  public StandardChatToolWindowTabPanel(
      @NotNull Project project,
      @NotNull Conversation conversation) {
    super(project, conversation, false);
    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    } else {
      displayConversation(conversation);
    }
  }

  @Override
  protected JComponent getLandingView() {
    return new StandardChatToolWindowLandingPanel((action, locationOnScreen) -> {
      var editor = EditorUtil.getSelectedEditor(project);
      if (editor == null || !editor.getSelectionModel().hasSelection()) {
        OverlayUtil.showWarningBalloon(
            editor == null ? "Unable to locate a selected editor"
                : "Please select a target code before proceeding",
            locationOnScreen);
        return;
      }

      var fileExtension = FileUtil.getFileExtension(
          ((EditorImpl) editor).getVirtualFile().getName());
      var message = new Message(action.getPrompt().replace(
          "{{selectedCode}}",
          format("\n```%s\n%s\n```", fileExtension, editor.getSelectionModel().getSelectedText())));
      message.setUserMessage(action.getUserMessage());

      sendMessage(message, ConversationType.DEFAULT);
    });
  }

  private void displayConversation(@NotNull Conversation conversation) {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var messageResponseBody =
          new ChatMessageResponseBody(project, this).withResponse(message.getResponse());

      var serpResults = message.getSerpResults();
      if (YouSettings.getInstance().getState().isDisplayWebSearchResults()
          && serpResults != null && !serpResults.isEmpty()) {
        messageResponseBody.displaySerpResults(serpResults);
      }
      messageResponseBody.hideCaret();

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(new UserMessagePanel(project, message, this));
      messagePanel.add(new ResponsePanel()
          .withReloadAction(() -> reloadMessage(message, conversation, ConversationType.DEFAULT))
          .withDeleteAction(() -> removeMessage(message.getId(), conversation))
          .addContent(messageResponseBody));
    });
  }
}
