package ee.carlrobert.chatgpt.ide.toolwindow.conversations;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

class RootConversationPanel extends JPanel {

  RootConversationPanel(Runnable onClick) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(JBUI.Borders.empty(10, 20));
    setBackground(JBColor.background());
    addMouseListener(getMouseListener(onClick));
  }

  private MouseListener getMouseListener(Runnable onClick) {
    return new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        onClick.run();
      }

      @Override
      public void mousePressed(MouseEvent mouseEvent) {
      }

      @Override
      public void mouseReleased(MouseEvent mouseEvent) {
      }

      @Override
      public void mouseEntered(MouseEvent mouseEvent) {
      }

      @Override
      public void mouseExited(MouseEvent mouseEvent) {
      }
    };
  }
}
