package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.lang.String.format;

import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.BaseChatToolWindowTabPanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserMessagePanel;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandardChatToolWindowTabPanel extends BaseChatToolWindowTabPanel {

  public StandardChatToolWindowTabPanel(@NotNull Project project) {
    this(project, null);
  }

  public StandardChatToolWindowTabPanel(
      @NotNull Project project,
      @Nullable Conversation conversation) {
    super(project, false);
    if (conversation == null) {
      displayLandingView();
    } else {
      displayConversation(conversation);
    }
  }

  @Override
  protected JComponent getLandingView() {
    return new StandardChatToolWindowLandingPanel((action, locationOnScreen) -> {
      var editor = EditorUtils.getSelectedEditor(project);
      if (editor == null || !editor.getSelectionModel().hasSelection()) {
        OverlayUtils.showWarningBalloon(
            editor == null ? "Unable to locate a selected editor"
                : "Please select a target code before proceeding",
            locationOnScreen);
        return;
      }

      var fileExtension = FileUtils.getFileExtension(
          ((EditorImpl) editor).getVirtualFile().getName());
      var message = new Message(action.getPrompt().replace(
          "{{selectedCode}}",
          format("\n```%s\n%s\n```", fileExtension, editor.getSelectionModel().getSelectedText())));
      message.setUserMessage(action.getUserMessage());

      if (conversation == null) {
        startNewConversation(message);
      } else {
        sendMessage(message);
      }
    });
  }

  @Override
  public void displayConversation(@NotNull Conversation conversation) {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var messageResponseBody = new ChatMessageResponseBody(project, this)
          .withResponse(message.getResponse());

      var serpResults = message.getSerpResults();
      if (YouSettingsState.getInstance().isDisplayWebSearchResults() &&
          serpResults != null && !serpResults.isEmpty()) {
        messageResponseBody.displaySerpResults(serpResults);
      }

      var messageWrapper = createNewMessageWrapper(message.getId());
      messageWrapper.add(new UserMessagePanel(project, message, this));
      messageWrapper.add(new ResponsePanel()
          .withReloadAction(() -> reloadMessage(message, conversation))
          .withDeleteAction(() -> removeMessage(message.getId(), messageWrapper, conversation))
          .addContent(messageResponseBody));
    });
    setConversation(conversation);
  }
}
