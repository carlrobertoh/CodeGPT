package ee.carlrobert.codegpt.settings.service;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServerProgressPanel extends JPanel {

  private final JBLabel label = new JBLabel();

  public ServerProgressPanel() {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
    setVisible(false);
    add(new AsyncProcessIcon("sign_in_spinner"));
    add(Box.createHorizontalStrut(4));
    add(label);
  }

  public void startProgress(String text) {
    setVisible(true);
    updateText(text);
  }

  public void updateText(String text) {
    label.setText(text);
  }

  public void displayComponent(JComponent component) {
    removeAll();
    add(component);
    revalidate();
    repaint();
  }
}
