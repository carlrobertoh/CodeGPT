package ee.carlrobert.chatgpt.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.chatgpt.service.ToolWindowService;
import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    // TODO: Get rid of
    var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
    ScrollablePanel scrollablePanel = new ScrollablePanel();
    toolWindowService.setScrollablePanel(scrollablePanel);
    this.refreshView();
  }

  public void handleSubmit() {
    var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
    var searchText = textField.getText();
    toolWindowService.sendMessage(searchText, searchText, () -> {
      scrollToBottom(scrollPane);
    });

    textField.setText("");
    scrollToBottom(scrollPane);
  }

  // TODO: Get rid of
  public void refreshView() {
    ScrollablePanel scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
    scrollablePanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);

    var toolWindowService = ApplicationManager.getApplication().getService(ToolWindowService.class);
    toolWindowService.setScrollablePanel(scrollablePanel);

    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(scrollablePanel);

    // TODO: Move to TextField class
    textField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          handleSubmit();
        }
      }
    });
  }

  public JPanel getContent() {
    return chatGptToolWindowContent;
  }


  private void scrollToBottom(JScrollPane scrollPane) {
    // TODO: this.scrollPanel.getVerticalScrollBar();
    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
    AdjustmentListener downScroller = new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        Adjustable adjustable = e.getAdjustable();
        adjustable.setValue(adjustable.getMaximum());
        verticalBar.removeAdjustmentListener(this);
      }
    };
    verticalBar.addAdjustmentListener(downScroller);
  }

  private void createUIComponents() {
    textField = new TextField(e -> handleSubmit());
    scrollPane = new JBScrollPane();
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
  }
}
