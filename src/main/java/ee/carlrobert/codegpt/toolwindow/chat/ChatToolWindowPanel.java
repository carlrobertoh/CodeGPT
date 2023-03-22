package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.SwingUtils.createIconLabel;
import static ee.carlrobert.codegpt.util.SwingUtils.createTextPane;
import static ee.carlrobert.codegpt.util.SwingUtils.justifyLeft;
import static java.lang.String.format;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.codegpt.account.AccountDetailsState;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.SettingsConfigurable;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.ToolWindowService;
import ee.carlrobert.codegpt.toolwindow.components.GenerateButton;
import ee.carlrobert.codegpt.toolwindow.components.LandingView;
import ee.carlrobert.codegpt.toolwindow.components.ScrollPane;
import ee.carlrobert.codegpt.toolwindow.components.SyntaxTextArea;
import ee.carlrobert.codegpt.toolwindow.components.TextArea;
import icons.Icons;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel {

  private static final List<SyntaxTextArea> textAreas = new ArrayList<>();
  private final Project project;
  private JPanel chatGptToolWindowContent;
  private ScrollPane scrollPane;
  private ScrollablePanel scrollablePanel;
  private JTextArea textArea;
  private JScrollPane textAreaScrollPane;
  private GenerateButton generateButton;
  private boolean isLandingViewVisible;
  private UUID conversationId;

  public ChatToolWindowPanel(@NotNull Project project) {
    this.project = project;
  }

  public JPanel getContent() {
    return chatGptToolWindowContent;
  }

  public void displayUserMessage(String userMessage) {
    addIconLabel(AllIcons.General.User, AccountDetailsState.getInstance().accountName);
    scrollablePanel.add(createTextPane(userMessage));
    scrollablePanel.revalidate();
    scrollablePanel.repaint();
  }

  public void displayLandingView() {
    if (!isLandingViewVisible) {
      SwingUtilities.invokeLater(() -> {
        clearWindow();
        isLandingViewVisible = true;

        addSpacing(16);
        var landingView = new LandingView();
        scrollablePanel.add(landingView.createImageIconPanel());
        addSpacing(16);
        landingView.getQuestionPanels().forEach(panel -> {
          scrollablePanel.add(panel);
          addSpacing(16);
        });
        scrollablePanel.revalidate();
        scrollablePanel.repaint();
      });
    }
  }

  public void displayConversation(Conversation conversation) {
    setConversationId(conversation.getId());
    clearWindow();
    conversation.getMessages().forEach(message -> {
      displayUserMessage(message.getPrompt());
      addIconLabel(Icons.DefaultImageIcon, "ChatGPT");
      var textArea = new SyntaxTextArea(true, false, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      textArea.setText(message.getResponse());
      textArea.displayCopyButton();
      scrollablePanel.add(textArea);
      textAreas.add(textArea);
    });
    scrollToBottom();
    scrollablePanel.revalidate();
    scrollablePanel.repaint();
  }

  public void sendMessage(String prompt, Project project) {
    sendMessage(prompt, project, false);
  }

  public void sendMessage(String prompt, Project project, boolean isRetry) {
    if (!isRetry) {
      addIconLabel(Icons.DefaultImageIcon, "ChatGPT");
    }

    var settings = SettingsState.getInstance();
    if (settings.apiKey.isEmpty()) {
      notifyMissingCredential(project, "API key not provided.");
    } else {
      SyntaxTextArea textArea;
      if (isRetry) {
        textArea = textAreas.get(textAreas.size() - 1);
        textArea.clear();
      } else {
        textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        addTextArea(textArea);
      }

      var conversation = ConversationsState.getInstance().getOrStartNew();
      setConversationId(conversation.getId());
      project.getService(ToolWindowService.class)
          .startRequest(prompt, textArea, project, isRetry, this, conversation);
    }
  }

  public void clearWindow() {
    isLandingViewVisible = false;
    generateButton.setVisible(false);
    textAreas.clear();
    scrollablePanel.removeAll();
  }

  public void addSpacing(int height) {
    scrollablePanel.add(Box.createVerticalStrut(height));
  }

  public void addIconLabel(Icon icon, String text) {
    addSpacing(8);
    scrollablePanel.add(justifyLeft(createIconLabel(icon, text)));
    addSpacing(8);
  }

  public void notifyMissingCredential(Project project, String text) {
    var label = new JLabel(format("<html>%s <font color='#589df6'><u>Open Settings</u></font> to set one.</html>", text));
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    label.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
      }
    });
    scrollablePanel.add(justifyLeft(label));
  }

  public void displayGenerateButton(Runnable onClick) {
    generateButton.setVisible(true);
    generateButton.setMode(GenerateButton.Mode.STOP, onClick);
  }

  public void stopGenerating(String prompt, SyntaxTextArea textArea, Project project) {
    generateButton.setMode(GenerateButton.Mode.REFRESH, () -> {
      sendMessage(prompt, project, true);
      scrollToBottom();
    });
    textArea.displayCopyButton();
    textArea.getCaret().setVisible(false);
    scrollToBottom();
  }

  public void scrollToBottom() {
    scrollPane.scrollToBottom();
  }

  public void addTextArea(SyntaxTextArea textArea) {
    scrollablePanel.add(textArea);
    textAreas.add(textArea);
  }

  public void changeStyle() {
    for (var textArea : textAreas) {
      textArea.changeStyleViaThemeXml();
    }
  }

  private void handleSubmit() {
    var searchText = textArea.getText();
    if (isLandingViewVisible || ConversationsState.getCurrentConversation() == null) {
      clearWindow();
    }
    displayUserMessage(searchText);
    sendMessage(searchText, project);
    textArea.setText("");
    scrollToBottom();
    scrollablePanel.revalidate();
    scrollablePanel.repaint();
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

    scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
    scrollPane = new ScrollPane(scrollablePanel);

    generateButton = new GenerateButton();
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }
}
