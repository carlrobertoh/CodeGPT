package ee.carlrobert.codegpt.toolwindow.chat.html;

import static com.intellij.openapi.ui.Messages.CANCEL;
import static com.intellij.openapi.ui.Messages.OK;
import static icons.Icons.DefaultImageIcon;

import com.intellij.execution.ExecutionBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DoNotAskOption;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.codegpt.client.RequestHandler;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.ToolWindowTabPanel;
import ee.carlrobert.codegpt.toolwindow.components.GenerateButton;
import ee.carlrobert.codegpt.toolwindow.components.TextArea;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatToolWindowTabHtmlPanel implements ToolWindowTabPanel {

  private static final Logger LOG = LoggerFactory.getLogger(ChatToolWindowTabHtmlPanel.class);
  private final Project project;

  private JPanel rootPanel;
  private JTextArea textArea;
  private JScrollPane textAreaScrollPane;
  private JPanel contentPanel;
  private JButton reloadResponseButton;
  private Conversation conversation;
  private MarkdownJCEFHtmlPanel markdownHtmlPanel;

  public ChatToolWindowTabHtmlPanel(@NotNull Project project) {
    this.project = project;
  }

  @Override
  public JPanel getContent() {
    return rootPanel;
  }

  @Override
  public Conversation getConversation() {
    return conversation;
  }

  @Override
  public void displayLandingView() {
    markdownHtmlPanel.displayLandingView();
  }

  @Override
  public void displayConversation(Conversation conversation) {
    this.conversation = conversation;
    markdownHtmlPanel.displayConversation(conversation);
  }

  @Override
  public void startNewConversation(Message message) {
    markdownHtmlPanel.runWhenLoaded(() -> {
      conversation = ConversationsState.getInstance().startConversation();
      startConversation(message, false);
    });
  }

  @Override
  public void startConversation(Message message, boolean isRetry) {
    markdownHtmlPanel.runWhenLoaded(() -> {
      markdownHtmlPanel.displayUserMessage(message);
      var settings = SettingsState.getInstance();
      if (settings.getApiKey().isEmpty()) {
        markdownHtmlPanel.displayMissingCredential(message.getId());
      } else {
        SwingUtilities.invokeLater(() -> call(message, isRetry));
      }
    });
  }

  // Ugly workaround for focusing JBCefBrowser on tab changes
  public void refreshMarkdownPanel() {
    contentPanel.removeAll();
    contentPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
    contentPanel.setPreferredSize(markdownHtmlPanel.getComponent().getPreferredSize());
    contentPanel.repaint();
    contentPanel.revalidate();
  }

  public void dispose() {
    markdownHtmlPanel.dispose();
  }

  private void call(Message message, boolean isRetry) {
    if (conversation == null) {
      conversation = ConversationsState.getInstance().startConversation();
    }

    var responseId = markdownHtmlPanel.prepareResponse(message.getId(), true, isRetry);
    var requestService = new RequestHandler(conversation) {
      public void handleMessage(String response, String fullMessage) {
        try {
          markdownHtmlPanel.replaceHtml(responseId, fullMessage);
        } catch (Exception e) {
          LOG.error("Error while replacing the html content", e);
          markdownHtmlPanel.displayErrorMessage();
          throw new RuntimeException(e);
        }
      }

      public void handleComplete() {
        stop();
      }

      public void handleTokensExceeded() {
        SwingUtilities.invokeLater(() -> {
          var answer = showTokenLimitExceededDialog();
          if (answer == OK) {
            conversation.discardTokenLimits();
            ConversationsState.getInstance().saveConversation(conversation);
            call(message, true);
          } else {
            stop();
          }
        });
      }

      public void handleError(ErrorDetails error) {
        stop();
        markdownHtmlPanel.displayErrorMessage(error.getMessage());
      }

      private void stop() {
        stopGenerating(message);
        markdownHtmlPanel.stopTyping();
        markdownHtmlPanel.updateReplaceButton();
      }
    };
    requestService.call(message, isRetry);
    displayReloadResponseButton(requestService::cancel);
  }

  private void handleSubmit() {
    startConversation(new Message(textArea.getText()), false);
    textArea.setText("");
  }

  private void displayReloadResponseButton(Runnable onClick) {
    var button = getReloadResponseButton();
    button.setVisible(true);
    button.setMode(GenerateButton.Mode.STOP, onClick);
  }

  private void stopGenerating(Message message) {
    getReloadResponseButton().setMode(GenerateButton.Mode.REFRESH, () -> call(message, true));
  }

  private GenerateButton getReloadResponseButton() {
    return ((GenerateButton) reloadResponseButton);
  }

  private int showTokenLimitExceededDialog() {
    return MessageDialogBuilder.okCancel("Token Limit Exceeded",
            "The maximum default token limit has been reached. Do you want to proceed with the conversation despite the higher messaging cost?")
        .yesText("Continue")
        .noText("Cancel")
        .icon(DefaultImageIcon)
        .doNotAsk(new DoNotAskOption.Adapter() {
          @Override
          public void rememberChoice(boolean isSelected, int exitCode) {
            if (isSelected) {
              ConversationsState.getInstance().discardAllTokenLimits();
            }
          }

          @NotNull
          @Override
          public String getDoNotShowMessage() {
            return ExecutionBundle.message("don.t.ask.again");
          }

          @Override
          public boolean shouldSaveOptionsOnCancel() {
            return true;
          }
        })
        .guessWindowAndAsk() ? OK : CANCEL;
  }

  private void createUIComponents() {
    textAreaScrollPane = new JBScrollPane() {
      public JScrollBar createVerticalScrollBar() {
        JScrollBar verticalScrollBar = new JScrollPane.ScrollBar(1);
        verticalScrollBar.setPreferredSize(new Dimension(0, 0));
        return verticalScrollBar;
      }
    };
    textAreaScrollPane.setBorder(null);
    textAreaScrollPane.setViewportBorder(null);
    textAreaScrollPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.border()),
        BorderFactory.createEmptyBorder(0, 5, 0, 10)));
    textArea = new TextArea(this::handleSubmit, textAreaScrollPane);
    textAreaScrollPane.setViewportView(textArea);
    markdownHtmlPanel = new MarkdownJCEFHtmlPanel(project);
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
    contentPanel.setPreferredSize(markdownHtmlPanel.getComponent().getPreferredSize());
    reloadResponseButton = new GenerateButton();
  }
}
