package ee.carlrobert.codegpt.util;

import static com.intellij.openapi.util.SystemInfo.isMac;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.system.CpuArch;
import com.intellij.util.ui.UI;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SwingUtils {

  public static JTextPane createTextPane(HyperlinkListener listener) {
    var textPane = new JTextPane();
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.addHyperlinkListener(listener);
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    return textPane;
  }

  public static JButton createIconButton(Icon icon) {
    var button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    return button;
  }

  public static void setEqualLabelWidths(JPanel firstPanel, JPanel secondPanel) {
    var firstLabel = firstPanel.getComponents()[0];
    var secondLabel = secondPanel.getComponents()[0];
    if (firstLabel instanceof JLabel && secondLabel instanceof JLabel) {
      firstLabel.setPreferredSize(secondLabel.getPreferredSize());
    }
  }

  public static JPanel createPanel(JComponent component, String label, boolean resizeX) {
    return UI.PanelFactory.panel(component)
        .withLabel(label)
        .resizeX(resizeX)
        .createPanel();
  }

  public static void handleHyperlinkClicked(HyperlinkEvent event) {
    if (ACTIVATED.equals(event.getEventType()) && event.getURL() != null) {
      try {
        BrowserUtil.browse(event.getURL().toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void addShiftEnterInputMap(JTextArea textArea, Runnable onSubmit) {
    var enterStroke = KeyStroke.getKeyStroke("ENTER");
    var shiftEnterStroke = KeyStroke.getKeyStroke("shift ENTER");
    textArea.getInputMap().put(shiftEnterStroke, "insert-break");
    textArea.getInputMap().put(enterStroke, "text-submit");
    textArea.getActionMap().put("text-submit", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        onSubmit.run();
      }
    });
  }
}

