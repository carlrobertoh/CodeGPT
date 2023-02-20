package ee.carlrobert.chatgpt.ide.toolwindow;

import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createIconLabel;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createTextArea;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.justifyLeft;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.wm.ToolWindow;
import ee.carlrobert.chatgpt.EmptyCallback;
import ee.carlrobert.chatgpt.client.ApiClient;
import ee.carlrobert.chatgpt.ide.settings.SettingsConfigurable;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ToolWindowService {

  private ScrollablePanel scrollablePanel;
  private ToolWindow toolWindow;
  private boolean isLandingViewVisible;

  public void setScrollablePanel(ScrollablePanel scrollablePanel) {
    this.scrollablePanel = scrollablePanel;
  }

  public void setToolWindow(ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
  }

  public ToolWindow getToolWindow() {
    return toolWindow;
  }

  public void paintUserMessage(String userMessage) {
    if (isLandingViewVisible) {
      removeAll();
    }
    addIconLabel("/icons/user-icon.png", "User:");
    addSpacing(8);
    scrollablePanel.add(createTextArea(userMessage, true));
  }

  public void sendMessage(String prompt, Project project, @Nullable EmptyCallback scrollToBottom) {
    addSpacing(16);
    addIconLabel("/icons/chatgpt-icon.png", "ChatGPT:");
    addSpacing(8);

    var secretKey = SettingsState.getInstance().secretKey;
    if (secretKey == null || secretKey.isEmpty()) {
      var label = new JLabel("<html>API key not provided. <font color='#589df6'><u>Open Settings</u></font> to set one.</html>");
      label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      label.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
        }
      });
      scrollablePanel.add(justifyLeft(label));
    } else {
      var textArea = createTextArea("", false);
      scrollablePanel.add(textArea);

      ApiClient.getInstance().getCompletionsAsync(prompt, (message) -> {
        textArea.append(message);
        if (scrollToBottom != null) {
          scrollToBottom.call();
        }
      });
    }

    addSpacing(16);
  }

  public void paintLandingView() {
    isLandingViewVisible = true;

    var imageIconPanel = new JPanel();
    imageIconPanel.setLayout(new GridBagLayout());
    var imageIconLabel = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/sun-icon.png"))));
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
    scrollablePanel.removeAll();
  }

  public void addSpacing(int height) {
    scrollablePanel.add(Box.createVerticalStrut(height));
  }

  public void addIconLabel(String path, String text) {
    scrollablePanel.add(justifyLeft(createIconLabel(Objects.requireNonNull(getClass().getResource(path)), text)));
  }
}
