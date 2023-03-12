package ee.carlrobert.codegpt.ide.toolwindow;

import static ee.carlrobert.codegpt.ide.toolwindow.components.SwingUtils.createIconLabel;
import static ee.carlrobert.codegpt.ide.toolwindow.components.SwingUtils.createTextArea;
import static ee.carlrobert.codegpt.ide.toolwindow.components.SwingUtils.justifyLeft;
import static java.lang.String.format;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.codegpt.ide.conversations.Conversation;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.settings.SettingsConfigurable;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import ee.carlrobert.codegpt.ide.toolwindow.components.GenerateButton;
import ee.carlrobert.codegpt.ide.toolwindow.components.LandingView;
import ee.carlrobert.codegpt.ide.toolwindow.components.ScrollPane;
import ee.carlrobert.codegpt.ide.toolwindow.components.SyntaxTextArea;
import ee.carlrobert.codegpt.ide.toolwindow.components.TextArea;
import icons.Icons;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;

public class ChatGptToolWindow {

  private static final List<SyntaxTextArea> textAreas = new ArrayList<>();
  private final Project project;
  private final ToolWindow toolWindow;
  private JPanel chatGptToolWindowContent;
  private ScrollPane scrollPane;
  private ScrollablePanel scrollablePanel;
  private JTextArea textArea;
  private JScrollPane textAreaScrollPane;
  private GenerateButton generateButton;
  private boolean isLandingViewVisible;

  public ChatGptToolWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    this.project = project;
    this.toolWindow = toolWindow;
  }

  public JPanel getContent() {
    return chatGptToolWindowContent;
  }

  public void show() {
    toolWindow.show();
  }

  public void displayUserMessage(String userMessage) {
    if (isLandingViewVisible || ConversationsState.getCurrentConversation() == null) {
      clearWindow();
    }

    addIconLabel(AllIcons.General.User, "User:");
    scrollablePanel.add(createTextArea(userMessage));
    scrollablePanel.validate();
    scrollablePanel.repaint();
  }

  public void displayLandingView() {
    isLandingViewVisible = true;
    clearWindow();

    var landingView = new LandingView();
    scrollablePanel.add(landingView.createImageIconPanel());
    addSpacing(16);
    landingView.getQuestionPanels().forEach(panel -> {
      scrollablePanel.add(panel);
      addSpacing(16);
    });

    scrollablePanel.validate();
    scrollablePanel.repaint();
  }

  public void displayConversation(Conversation conversation) {
    clearWindow();

    conversation.getMessages().forEach(message -> {
      displayUserMessage(message.getPrompt());

      addIconLabel(Icons.DefaultImageIcon, "ChatGPT:");
      var textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      textArea.setText(message.getResponse());
      textArea.displayCopyButton();
      textArea.hideCaret();
      scrollablePanel.add(textArea);
      textAreas.add(textArea);
    });

    scrollToBottom();
    scrollablePanel.validate();
    scrollablePanel.repaint();
  }

  public void displayResponse(String prompt) {
    addIconLabel(Icons.DefaultImageIcon, "ChatGPT:");

    var settings = SettingsState.getInstance();
    if (settings.isGPTOptionSelected && settings.apiKey.isEmpty()) {
      notifyMissingCredential(project, "API key not provided.");
    } else if (settings.isChatGPTOptionSelected && settings.accessToken.isEmpty()) {
      notifyMissingCredential(project, "Access token not provided.");
    } else {
      var textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      addTextArea(textArea);
    }
  }

  public void clearWindow() {
    isLandingViewVisible = false;
    generateButton.setVisible(false);
    scrollablePanel.removeAll();
    textAreas.clear();
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

  public void stopGenerating(SyntaxTextArea textArea, Runnable onRefresh) {
    generateButton.setMode(GenerateButton.Mode.REFRESH, onRefresh);
    textArea.displayCopyButton();
    textArea.hideCaret();
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

    displayUserMessage(searchText);

    displayResponse()
    project.getService(ToolWindowService.class).sendMessage(searchText, project);


    textArea.setText("");
    scrollToBottom();
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
    textAreaScrollPane.setViewportView(textArea);

    textArea = new TextArea(this::handleSubmit, textAreaScrollPane);

    scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

    scrollPane = new ScrollPane(scrollablePanel);
    generateButton = new GenerateButton();

    displayLandingView();
  }
}
