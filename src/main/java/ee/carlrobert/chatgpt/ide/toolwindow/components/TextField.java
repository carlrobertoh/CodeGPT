package ee.carlrobert.chatgpt.ide.toolwindow.components;

import com.intellij.ui.JBColor;
import ee.carlrobert.chatgpt.EmptyCallback;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

public class TextField extends JTextField {

  public TextField(EmptyCallback onSubmit) {
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(),
        BorderFactory.createEmptyBorder(5, 5, 5, 10)));
    setForeground(JBColor.GRAY);
    addFocusListener(getFocusListener());
    addKeyListener(getKeyListener(onSubmit));
    addSubmitButton(onSubmit);
  }

  private void addSubmitButton(EmptyCallback onSubmit) {
    var button = createSubmitButton(e -> onSubmit.call());
    ComponentBorder cb = new ComponentBorder(button);
    cb.setAdjustInsets(false);
    cb.install(this);
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

  private KeyListener getKeyListener(EmptyCallback onSubmit) {
    return new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          onSubmit.call();
        }
      }
    };
  }
}
