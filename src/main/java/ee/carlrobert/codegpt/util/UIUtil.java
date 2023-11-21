package ee.carlrobert.codegpt.util;

import static com.intellij.util.ui.UIUtil.isUnderDarcula;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.toolwindow.chat.components.SmartScroller;
import java.awt.Color;
import java.awt.Dimension;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class UIUtil {

  public static JTextPane createTextPane(String text) {
    return createTextPane(text, UIUtil::handleHyperlinkClicked);
  }

  public static JTextPane createTextPane(String text, HyperlinkListener listener) {
    var textPane = new JTextPane();
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.addHyperlinkListener(listener);
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    textPane.setText(text);
    return textPane;
  }

  public static JButton createIconButton(Icon icon) {
    var button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    return button;
  }

  public static JScrollPane createScrollPaneWithSmartScroller(ScrollablePanel scrollablePanel) {
    var scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    new SmartScroller(scrollPane);
    return scrollPane;
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
    var url = event.getURL();
    if (ACTIVATED.equals(event.getEventType()) && url != null) {
      try {
        BrowserUtil.browse(url.toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void addShiftEnterInputMap(JTextArea textArea, AbstractAction onSubmit) {
    textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
    textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "text-submit");
    textArea.getActionMap().put("text-submit", onSubmit);
  }

  public static Color getPanelBackgroundColor() {
    return isUnderDarcula()
        ? ColorUtil.darker(JBColor.PanelBackground, 1)
        : JBColor.PanelBackground.brighter();
  }
}

