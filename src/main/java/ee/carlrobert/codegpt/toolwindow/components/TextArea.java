package ee.carlrobert.codegpt.toolwindow.components;

import static ee.carlrobert.codegpt.util.SwingUtils.addShiftEnterInputMap;
import static ee.carlrobert.codegpt.util.SwingUtils.createIconButton;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import icons.Icons;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class TextArea extends JBTextArea {

  public TextArea(Runnable onSubmit, JScrollPane textAreaScrollPane) {
    super();
    getEmptyText().setText("Ask me anything...");
    setForeground(JBColor.GRAY);
    setMargin(JBUI.insets(5));
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
}
