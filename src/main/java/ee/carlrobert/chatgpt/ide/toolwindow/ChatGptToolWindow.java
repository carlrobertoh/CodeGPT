package ee.carlrobert.chatgpt.ide.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.chatgpt.ide.toolwindow.components.GenerateButton;
import ee.carlrobert.chatgpt.ide.toolwindow.components.TextArea;
import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import org.jetbrains.annotations.NotNull;

public class ChatGptToolWindow {

  private final Project project;
  private JPanel chatGptToolWindowContent;
  private JScrollPane scrollPane;
  private JTextArea textArea;
  private JScrollPane textAreaScrollPane;
  private JButton generateButton;

  public ChatGptToolWindow(@NotNull Project project) {
    this.project = project;
  }

  public void handleSubmit() {
    var toolWindowService = project.getService(ToolWindowService.class);
    var searchText = textArea.getText();
    toolWindowService.paintUserMessage(searchText);

    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      executor.execute(() -> {
        toolWindowService.sendMessage(searchText, project, this::scrollToBottom);
        textArea.setText("");
        scrollToBottom();
      });
    } finally {
      executor.shutdown();
    }
  }

  public JPanel getContent() {
    return chatGptToolWindowContent;
  }

  private void scrollToBottom() {
    JScrollBar verticalBar = this.scrollPane.getVerticalScrollBar();
    verticalBar.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent e) {
        Adjustable adjustable = e.getAdjustable();
        adjustable.setValue(adjustable.getMaximum());
        verticalBar.removeAdjustmentListener(this);
      }
    });
  }

  private void createUIComponents() {
    textAreaScrollPane = new JBScrollPane() {
      @Override
      public JScrollBar createVerticalScrollBar() {
        JScrollBar verticalScrollBar = new JScrollPane.ScrollBar(1);
        verticalScrollBar.setPreferredSize(new Dimension(0, 0));
        return verticalScrollBar;
      }
    };
    textAreaScrollPane.setBorder(null);
    textAreaScrollPane.setViewportBorder(null);
    textAreaScrollPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(),
        BorderFactory.createEmptyBorder(0, 5, 0, 10)));
    textAreaScrollPane.setViewportView(textArea);

    textArea = new TextArea(this::handleSubmit, textAreaScrollPane);

    ScrollablePanel scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

    scrollPane = new JBScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(scrollablePanel);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);

    generateButton = new GenerateButton();

    var toolWindowService = project.getService(ToolWindowService.class);
    toolWindowService.setGenerateButton((GenerateButton) generateButton); // TODO: Remove casting
    toolWindowService.setScrollablePanel(scrollablePanel);
    toolWindowService.paintLandingView();
  }
}
