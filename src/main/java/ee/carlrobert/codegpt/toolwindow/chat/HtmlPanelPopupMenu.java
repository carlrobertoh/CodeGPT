package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.icons.AllIcons;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class HtmlPanelPopupMenu extends JPopupMenu {

  private final JMenuItem copyMenuItem;
  private final JMenuItem replaceMenuItem;

  HtmlPanelPopupMenu() {
    super();
    copyMenuItem = new JMenuItem("Copy", AllIcons.Actions.Copy);
    replaceMenuItem = new JMenuItem("Replace Editor Selection", AllIcons.Actions.Replace);
    add(copyMenuItem);
    add(replaceMenuItem);
  }

  MouseAdapter getMouseAdapter() {
    return new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showPopupMenu(e.getX(), e.getY());
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showPopupMenu(e.getX(), e.getY());
        }
      }

      private void showPopupMenu(int x, int y) {
        copyMenuItem.addActionListener(e -> {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          // String selection = (String) browser.executeJavaScriptAndReturnValue("window.getSelection().toString();").getValue();
          StringSelection data = new StringSelection("selection");
          clipboard.setContents(data, null);
        });
        replaceMenuItem.addActionListener(e -> {});
        show(getComponent(), x, y);
      }
    };
  }
}
