package ee.carlrobert.chatgpt.ide.toolwindow.components;

import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.addShiftEnterInputMap;
import static ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowUtil.createIconButton;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import icons.Icons;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class TextArea extends JBTextArea {

  public TextArea(Runnable onSubmit, JScrollPane textAreaScrollPane) {
    super("Ask me anything...");
    setForeground(JBColor.GRAY);
    setMargin(JBUI.insets(5));
    addFocusListener(getFocusListener());
    addSubmitButton(onSubmit, textAreaScrollPane);
    addShiftEnterInputMap(this, onSubmit);
  }

  private void addSubmitButton(Runnable onSubmit, JScrollPane textAreaScrollPane) {
    var button = createSubmitButton(e -> onSubmit.run());
    ComponentBorder cb = new ComponentBorder(button);
    cb.setAdjustInsets(true);
    cb.install(textAreaScrollPane);
  }

  private JButton createSubmitButton(ActionListener submitButtonListener) {
    var button = createIconButton(Icons.SendImageIcon);
    button.addActionListener(submitButtonListener);
    return button;
  }

  private FocusListener getFocusListener() {
    return new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (getText().equals("Ask me anything...")) {
          setText("");
          setForeground(JBColor.BLACK);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
          setForeground(JBColor.GRAY);
          setText("Ask me anything...");
        }
      }
    };
  }
}
