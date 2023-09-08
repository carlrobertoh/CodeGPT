package ee.carlrobert.codegpt.toolwindow.conversations;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

class RootConversationPanel extends JPanel {

  RootConversationPanel(Runnable onClick) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(JBUI.Borders.empty(10, 20));
    setBackground(JBColor.background());
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        onClick.run();
      }
    });
  }
}
