package ee.carlrobert.chatgpt.toolwindow;

import com.intellij.ui.JBColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

public class TextFieldFocusListener implements FocusListener {

  private final JTextField searchField;

  public TextFieldFocusListener(JTextField searchField) {
    this.searchField = searchField;
  }

  public void focusGained(FocusEvent event) {
    if (searchField.getText().equals("Ask a question...")) {
      searchField.setText("");
      searchField.setForeground(JBColor.BLACK);
    }
  }

  public void focusLost(FocusEvent event) {
    if (searchField.getText().isEmpty()) {
      searchField.setForeground(JBColor.GRAY);
      searchField.setText("Ask a question...");
    }
  }
}
