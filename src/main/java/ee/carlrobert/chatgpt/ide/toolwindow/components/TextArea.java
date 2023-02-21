package ee.carlrobert.chatgpt.ide.toolwindow.components;

import com.intellij.ui.JBColor;
import ee.carlrobert.chatgpt.EmptyCallback;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class TextArea extends JTextArea {

  public TextArea(EmptyCallback onSubmit, JScrollPane textAreaScrollPane) {
    setForeground(JBColor.GRAY);
    addFocusListener(getFocusListener());
    addSubmitButton(onSubmit, textAreaScrollPane);

    var input = getInputMap();
    var enterStroke = KeyStroke.getKeyStroke("ENTER");
    var shiftEnterStroke = KeyStroke.getKeyStroke("shift ENTER");
    input.put(shiftEnterStroke, "insert-break");
    input.put(enterStroke, "text-submit");

    var actions = getActionMap();
    actions.put("text-submit", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        onSubmit.call();
      }
    });
  }

  private void addSubmitButton(EmptyCallback onSubmit, JScrollPane textAreaScrollPane) {
    var button = createSubmitButton(e -> onSubmit.call());
    ComponentBorder cb = new ComponentBorder(button);
    cb.setAdjustInsets(true);
    cb.install(textAreaScrollPane);
  }

  private JButton createSubmitButton(ActionListener submitButtonListener) {
    var imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/send-icon.png")));
    var button = new JButton(imageIcon);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
    button.addActionListener(submitButtonListener);
    return button;
  }

  private FocusListener getFocusListener() {
    return new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (getText().equals("Ask a question...")) {
          setText("");
          setForeground(JBColor.BLACK);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
          setForeground(JBColor.GRAY);
          setText("Ask a question...");
        }
      }
    };
  }
}
