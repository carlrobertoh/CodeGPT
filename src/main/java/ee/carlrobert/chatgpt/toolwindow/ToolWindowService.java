package ee.carlrobert.chatgpt.toolwindow;

import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.createIconLabel;
import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.createTextArea;
import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.justifyLeft;

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import ee.carlrobert.chatgpt.EmptyCallback;
import ee.carlrobert.chatgpt.client.ApiClient;
import java.awt.GridBagLayout;
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
  private boolean isLandingViewVisible;

  public void setScrollablePanel(ScrollablePanel scrollablePanel) {
    this.scrollablePanel = scrollablePanel;
  }

  public ScrollablePanel getScrollablePanel() {
    return scrollablePanel;
  }

  public void paintUserMessage(String userMessage) {
    if (isLandingViewVisible) {
      removeAll();
    }
    scrollablePanel.add(justifyLeft(createIconLabel(Objects.requireNonNull(getClass().getResource("/icons/user-icon.png")), "User:")));
    scrollablePanel.add(Box.createVerticalStrut(8));
    scrollablePanel.add(createTextArea(userMessage, true));
  }

  public void sendMessage(String prompt, @Nullable EmptyCallback scrollToBottom) {
    scrollablePanel.add(Box.createVerticalStrut(16));
    scrollablePanel.add(justifyLeft(createIconLabel(Objects.requireNonNull(getClass().getResource("/icons/chatgpt-icon.png")), "ChatGPT:")));
    scrollablePanel.add(Box.createVerticalStrut(8));

    var textArea = createTextArea("", false);
    scrollablePanel.add(textArea);

    ApiClient.getInstance().getCompletionsAsync(prompt, (message) -> {
      textArea.append(message);
      if (scrollToBottom != null) {
        scrollToBottom.call();
      }
    });
    scrollablePanel.add(Box.createVerticalStrut(16));
  }

  public void paintLandingView() {
    isLandingViewVisible = true;

    var imageIconPanel = new JPanel();
    imageIconPanel.setLayout(new GridBagLayout());
    var imageIconLabel = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/sun-icon.png"))));
    imageIconLabel.setHorizontalAlignment(JLabel.CENTER);
    imageIconPanel.add(imageIconLabel);
    scrollablePanel.add(imageIconPanel);

    scrollablePanel.add(Box.createVerticalStrut(16));

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
      scrollablePanel.add(Box.createVerticalStrut(16));
    }
  }

  public void removeAll() {
    isLandingViewVisible = false;
    scrollablePanel.removeAll();
  }
}
