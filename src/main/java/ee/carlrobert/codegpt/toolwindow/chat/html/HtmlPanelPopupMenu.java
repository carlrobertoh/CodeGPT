package ee.carlrobert.codegpt.toolwindow.chat.html;

import com.intellij.icons.AllIcons;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.cef.browser.CefBrowser;

class HtmlPanelPopupMenu extends JPopupMenu {

  HtmlPanelPopupMenu(CefBrowser cefBrowser) {
    super();
    var copyMenuItem = new JMenuItem("Copy", AllIcons.Actions.Copy);
    copyMenuItem.addActionListener(e -> {
      cefBrowser.executeJavaScript("window.JavaPanelBridge.copyCode(window.getSelection().toString());", null, 0);
    });
    add(copyMenuItem);
  }

  MouseAdapter getMouseAdapter() {
    return new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          show(getComponent(), e.getX(), e.getY());
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          show(getComponent(), e.getX(), e.getY());
        }
      }
    };
  }
}
