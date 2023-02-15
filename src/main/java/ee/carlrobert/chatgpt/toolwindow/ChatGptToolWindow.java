package ee.carlrobert.chatgpt.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.chatgpt.toolwindow.components.TextField;
import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ChatGptToolWindow {

  private JPanel chatGptToolWindowContent;
  private JTextField textField;
  private JScrollPane scrollPane;

  public ChatGptToolWindow() {
  }

  public void handleSubmit() {
    var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
    var searchText = textField.getText();
    toolWindowService.sendMessage(searchText, searchText, this::scrollToBottom);
    textField.setText("");
    scrollToBottom();
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
    textField = new TextField(this::handleSubmit);

    ScrollablePanel scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

    scrollPane = new JBScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(scrollablePanel);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);

    var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
    toolWindowService.setScrollablePanel(scrollablePanel);
  }
}
