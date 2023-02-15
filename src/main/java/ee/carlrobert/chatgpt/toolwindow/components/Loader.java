package ee.carlrobert.chatgpt.toolwindow.components;

import com.intellij.util.ui.JBUI;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;

public class Loader {

  private final Timer timer;
  private final JLabel component;

  public Loader() {
    this.timer = new Timer("Loader");
    component = new JLabel(" ");
    component.setFont(new Font("Tahoma", Font.BOLD, 24));
    component.setBorder(JBUI.Borders.emptyLeft(2));
  }

  public void startLoading() {
    timer.schedule(new TimerTask() {
      public void run() {
        var loadingText = component.getText();
        if ("...".equals(loadingText)) {
          component.setText(" ");
        } else if (" ".equals(loadingText)) {
          component.setText(".");
        } else {
          component.setText(loadingText + ".");
        }
      }
    }, 500L, 500);
  }

  public void stopLoading() {
    timer.cancel();
    component.setVisible(false);
  }

  public JLabel getComponent() {
    return component;
  }
}
