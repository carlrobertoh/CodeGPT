package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.codegpt.client.RequestHandler;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.components.GenerateButton;
import ee.carlrobert.codegpt.toolwindow.components.TextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatToolWindowTabHtmlPanel implements ToolWindowTabPanel {

  private static final Logger LOG = LoggerFactory.getLogger(ChatToolWindowTabHtmlPanel.class);

  private final Editor editor;
  private JPanel rootPanel;
  private JTextArea textArea;
  private JScrollPane textAreaScrollPane;
  private JPanel contentPanel;
  private JButton reloadResponseButton;
  private JTextArea systemInitMessageArea;
  private Conversation conversation;
  private MarkdownJCEFHtmlPanel markdownHtmlPanel;

  public ChatToolWindowTabHtmlPanel(@Nullable Editor editor) {
    this.editor = editor;
  }

  // Ugly workaround for focusing JBCefBrowser on tab changes
  public void refreshMarkdownPanel() {
    contentPanel.removeAll();
    contentPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
    contentPanel.setPreferredSize(markdownHtmlPanel.getComponent().getPreferredSize());
    contentPanel.repaint();
    contentPanel.revalidate();
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
    systemInitMessageArea.setText(this.conversation.getSystemInitMessage());
    markdownHtmlPanel.displayConversation(conversation);
  }

  @Override
  public void startNewConversation(String prompt) {
    markdownHtmlPanel.runWhenLoaded(() -> {
      conversation = ConversationsState.getInstance().startConversation();

      startConversation(prompt, false);
    });
  }

  public void startConversation(String prompt, boolean isRetry,String systemRole ) {
    markdownHtmlPanel.displayUserMessage(prompt);
    var settings = SettingsState.getInstance();
    if (conversation == null) {
      conversation = ConversationsState.getInstance().startConversation();
    }

    conversation.systemInitMessage = systemRole;



    if (settings.apiKey.isEmpty()) {
      markdownHtmlPanel.displayMissingCredential();
    } else {
      SwingUtilities.invokeLater(() -> call(prompt, isRetry));
    }
  }


  @Override
  public void startConversation(String prompt, boolean isRetry) {

    markdownHtmlPanel.displayUserMessage(prompt);
    var settings = SettingsState.getInstance();
    if (settings.apiKey.isEmpty()) {
      markdownHtmlPanel.displayMissingCredential();
    } else {
      SwingUtilities.invokeLater(() -> call(prompt, isRetry));
    }


  }

  public void dispose() {
    markdownHtmlPanel.dispose();
  }

  private void call(String prompt, boolean isRetry) {
    if (conversation == null) {
      conversation = ConversationsState.getInstance().startConversation();
    }
    systemInitMessageArea.setText(conversation.getSystemInitMessage());
    var responseId = markdownHtmlPanel.prepareResponse(true, isRetry);
    var conversationMessage = new Message(prompt);
    var requestService = new RequestHandler(conversation) {
      public void handleMessage(String message, String fullMessage) {
        try {
          markdownHtmlPanel.replaceHtml(responseId, fullMessage);
        } catch (Exception e) {
          LOG.error("Error while replacing the html content", e);
          markdownHtmlPanel.displayErrorMessage();
          throw new RuntimeException(e);
        }
      }

      public void handleComplete() {
        stopGenerating(prompt);
        markdownHtmlPanel.stopTyping();
        markdownHtmlPanel.updateReplaceButton(editor);
      }

      public void handleError(String errorMessage) {
        markdownHtmlPanel.displayErrorMessage(errorMessage);
      }
    };
    requestService.call(conversationMessage, isRetry);
    displayReloadResponseButton(requestService::cancel);
  }

  private void handleSubmit() {

    startConversation(textArea.getText(), false,  systemInitMessageArea.getText());
    textArea.setText("");
  }

  private void displayReloadResponseButton(Runnable onClick) {
    var button = getReloadResponseButton();
    button.setVisible(true);
    button.setMode(GenerateButton.Mode.STOP, onClick);
  }

  private void stopGenerating(String prompt) {
    getReloadResponseButton().setMode(GenerateButton.Mode.REFRESH, () -> call(prompt, true));
  }

  private GenerateButton getReloadResponseButton() {
    return ((GenerateButton) reloadResponseButton);
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
    markdownHtmlPanel = new MarkdownJCEFHtmlPanel(editor);
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
    contentPanel.setPreferredSize(markdownHtmlPanel.getComponent().getPreferredSize());
    reloadResponseButton = new GenerateButton();

  }
}
