package ee.carlrobert.codegpt.settings.service.llama.form;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServerProgressPanel extends JPanel {

  private final JBLabel label = new JBLabel();
  private final AsyncProcessIcon loadingSpinner = new AsyncProcessIcon("sign_in_spinner");

  public void displayText(String text) {
    label.setText(text);
    removeAll();
    add(loadingSpinner);
    add(label);
    revalidate();
    repaint();
  }

  public void displayComponent(JComponent component) {
    removeAll();
    add(component);
    revalidate();
    repaint();
  }
}
