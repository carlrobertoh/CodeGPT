package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.Nullable;

class HtmlPanelPopupMenu extends JPopupMenu {

  HtmlPanelPopupMenu(@Nullable Editor editor, CefBrowser cefBrowser) {
    super();
    var copyMenuItem = new JMenuItem("Copy", AllIcons.Actions.Copy);
    copyMenuItem.addActionListener(e -> {
      cefBrowser.executeJavaScript("window.JavaPanelBridge.copyCode(window.getSelection().toString());", null, 0);
    });
    add(copyMenuItem);

    if (editor != null) {
      var replaceMenuItem = new JMenuItem("Replace Editor Selection", AllIcons.Actions.Replace);
      replaceMenuItem.addActionListener(e -> {
        cefBrowser.executeJavaScript("window.JavaPanelBridge.replaceCode(window.getSelection().toString());", null, 0);
      });
      add(replaceMenuItem);
    }
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
