package ee.carlrobert.chatgpt.ide.toolwindow;

import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createIconLabel;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createTextArea;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.justifyLeft;
import static java.lang.String.format;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import ee.carlrobert.chatgpt.client.ClientFactory;
import ee.carlrobert.chatgpt.ide.conversations.Conversation;
import ee.carlrobert.chatgpt.ide.conversations.ConversationsState;
import ee.carlrobert.chatgpt.ide.conversations.message.Message;
import ee.carlrobert.chatgpt.ide.settings.SettingsConfigurable;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import ee.carlrobert.chatgpt.ide.toolwindow.components.GenerateButton;
import ee.carlrobert.chatgpt.ide.toolwindow.components.GenerateButton.Mode;
import ee.carlrobert.chatgpt.ide.toolwindow.components.SyntaxTextArea;
import icons.Icons;
import java.awt.Adjustable;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;

public class ToolWindowService implements LafManagerListener {

  private static final List<SyntaxTextArea> textAreas = new ArrayList<>();
  private boolean isLandingViewVisible;
  private ScrollablePanel scrollablePanel;
  private JScrollPane scrollPane;
  private GenerateButton generateButton;

  public void setScrollablePanel(ScrollablePanel scrollablePanel) {
    this.scrollablePanel = scrollablePanel;
  }

  public void setScrollPane(JScrollPane scrollPane) {
    this.scrollPane = scrollPane;
  }

  public void setGenerateButton(GenerateButton generateButton) {
    this.generateButton = generateButton;
  }

  @Override
  public void lookAndFeelChanged(@NotNull LafManager source) {
    for (var textArea : textAreas) {
      textArea.changeStyleViaThemeXml();
    }
  }

  public ToolWindow getToolWindow(@NotNull Project project) {
    return ToolWindowManager.getInstance(project).getToolWindow("CodeGPT");
  }

  public void paintUserMessage(String userMessage) {
    if (isLandingViewVisible || ConversationsState.getCurrentConversation() == null) {
      removeAll();
    }
    addSpacing(8);
    addIconLabel(AllIcons.General.User, "User:");
    addSpacing(8);
    scrollablePanel.add(createTextArea(userMessage));
  }

  public void sendMessage(String prompt, Project project, @Nullable Runnable scrollToBottom) {
    addSpacing(8);
    addIconLabel(Icons.DefaultImageIcon, "ChatGPT:");
    addSpacing(8);

    var settings = SettingsState.getInstance();
    if (settings.isGPTOptionSelected && settings.apiKey.isEmpty()) {
      notifyMissingCredential(project, "API key not provided.");
    } else if (settings.isChatGPTOptionSelected && settings.accessToken.isEmpty()) {
      notifyMissingCredential(project, "Access token not provided.");
    } else {
      var conversationsState = ConversationsState.getInstance();
      var conversation = ConversationsState.getCurrentConversation();
      if (conversation == null) {
        conversation = conversationsState.startConversation();
      }

      var textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      scrollablePanel.add(textArea);
      textAreas.add(textArea);

      var client = new ClientFactory().getClient();
      generateButton.setVisible(true);
      generateButton.setMode(Mode.STOP, client::cancelRequest);

      var conversationMessage = new Message();
      conversationMessage.setPrompt(prompt);
      client.getCompletionsAsync(
          conversation,
          prompt,
          message -> {
            try {
              SwingUtilities.invokeAndWait(
                  () -> {
                    textArea.append(message);
                    // TODO: Should we set the text everytime?
                    conversationMessage.setResponse(textArea.getText());
                    if (scrollToBottom != null) {
                      scrollToBottom.run();
                    }
                  }
              );
            } catch (InterruptedException | InvocationTargetException e) {
              textArea.append("Something went wrong. Please try again later.");
              throw new RuntimeException(e);
            }
          },
          (completedConversation) -> {
            ConversationsState.getInstance().saveConversation(completedConversation);
            stopGenerating(prompt, textArea, project, scrollToBottom);
          },
          (errorMessage) -> {
            var currentConversation = ConversationsState.getCurrentConversation();
            conversationMessage.setResponse(errorMessage);
            currentConversation.addMessage(conversationMessage);
            ConversationsState.getInstance().saveConversation(currentConversation);

            textArea.append(errorMessage);
            stopGenerating(prompt, textArea, project, scrollToBottom);
          });
    }
  }

  public void displayConversation(Conversation conversation) {
    removeAll();
    conversation.getMessages().forEach(message -> {
      paintUserMessage(message.getPrompt());

      addSpacing(8);
      addIconLabel(Icons.DefaultImageIcon, "ChatGPT:");
      addSpacing(8);

      var textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      textArea.setText(message.getResponse());
      textArea.displayCopyButton();
      textArea.hideCaret();
      scrollablePanel.add(textArea);
      textAreas.add(textArea);
    });
    scrollToBottom();
  }

  public void paintLandingView() {
    isLandingViewVisible = true;

    var imageIconPanel = new JPanel();
    imageIconPanel.setLayout(new GridBagLayout());
    var imageIconLabel = new JLabel(Icons.SunImageIcon);
    imageIconLabel.setHorizontalAlignment(JLabel.CENTER);
    imageIconPanel.add(imageIconLabel);
    scrollablePanel.add(imageIconPanel);

    addSpacing(16);

    var questions = List.of("How do I make an HTTP request in Javascript?",
        "What is the difference between px, dip, dp, and sp?",
        "How do I undo the most recent local commits in Git?",
        "What is the difference between stack and heap?");
    for (var question : questions) {
      var panel = new JPanel();
      panel.setLayout(new GridBagLayout());
      var label = new JLabel(question, SwingConstants.CENTER);
      label.setHorizontalAlignment(JLabel.CENTER);
      panel.add(label);
      scrollablePanel.add(panel);
      addSpacing(16);
    }
  }

  public void removeAll() {
    isLandingViewVisible = false;
    generateButton.setVisible(false);
    scrollablePanel.removeAll();
  }

  private void stopGenerating(String prompt, SyntaxTextArea textArea, Project project, @Nullable Runnable scrollToBottom) {
    generateButton.setMode(Mode.REFRESH, () -> {
      sendMessage(prompt, project, scrollToBottom);
      if (scrollToBottom != null) {
        scrollToBottom.run();
      }
    });
    textArea.displayCopyButton();
    textArea.hideCaret();
    if (scrollToBottom != null) {
      scrollToBottom.run();
    }
  }

  private void addSpacing(int height) {
    scrollablePanel.add(Box.createVerticalStrut(height));
  }

  private void addIconLabel(Icon icon, String text) {
    scrollablePanel.add(justifyLeft(createIconLabel(icon, text)));
  }

  private void notifyMissingCredential(Project project, String text) {
    var label = new JLabel(format("<html>%s <font color='#589df6'><u>Open Settings</u></font> to set one.</html>", text));
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    label.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
      }
    });
    scrollablePanel.add(justifyLeft(label));
  }

  private void scrollToBottom() {
    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
    verticalBar.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent e) {
        Adjustable adjustable = e.getAdjustable();
        adjustable.setValue(adjustable.getMaximum());
        verticalBar.removeAdjustmentListener(this);
      }
    });
  }
}
