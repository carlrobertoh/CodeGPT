package ee.carlrobert.chatgpt.ide.toolwindow;

import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createIconLabel;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createTextArea;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.justifyLeft;
import static java.lang.String.format;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import ee.carlrobert.chatgpt.client.ClientFactory;
import ee.carlrobert.chatgpt.ide.settings.SettingsConfigurable;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import ee.carlrobert.chatgpt.ide.toolwindow.components.GenerateButton;
import ee.carlrobert.chatgpt.ide.toolwindow.components.GenerateButton.Mode;
import ee.carlrobert.chatgpt.ide.toolwindow.components.SyntaxTextArea;
import icons.Icons;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;

public class ToolWindowService implements LafManagerListener {

  private static final List<SyntaxTextArea> textAreas = new ArrayList<>();
  private boolean isLandingViewVisible;
  private ScrollablePanel scrollablePanel;
  private GenerateButton generateButton;

  public void setScrollablePanel(ScrollablePanel scrollablePanel) {
    this.scrollablePanel = scrollablePanel;
  }

  public void setGenerateButton(GenerateButton generateButton) {
    this.generateButton = generateButton;
  }

  public ToolWindow getToolWindow(@NotNull Project project) {
    return ToolWindowManager.getInstance(project).getToolWindow("CodeGPT");
  }

  public void paintUserMessage(String userMessage) {
    if (isLandingViewVisible) {
      removeAll();
    }
    addSpacing(8);
    addIconLabel(Icons.UserImageIcon, "User:");
    addSpacing(8);
    scrollablePanel.add(createTextArea(userMessage, true));
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
      var textArea = new SyntaxTextArea(true, true, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
      scrollablePanel.add(textArea);
      textAreas.add(textArea);

      var client = new ClientFactory().getClient();
      generateButton.setVisible(true);
      generateButton.setMode(Mode.STOP, client::cancelRequest);
      client.getCompletionsAsync(prompt, message -> {
        try {
          SwingUtilities.invokeAndWait(
              () -> {
                textArea.append(message);
                if (scrollToBottom != null) {
                  scrollToBottom.run();
                }
              }
          );
        } catch (InterruptedException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }, () -> {
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
      });
    }
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

  private void addSpacing(int height) {
    scrollablePanel.add(Box.createVerticalStrut(height));
  }

  private void addIconLabel(ImageIcon imageIcon, String text) {
    scrollablePanel.add(justifyLeft(createIconLabel(imageIcon, text)));
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

  @Override
  public void lookAndFeelChanged(@NotNull LafManager source) {
    for (var textArea : textAreas) {
      textArea.changeStyleViaThemeXml();
    }
  }
}
