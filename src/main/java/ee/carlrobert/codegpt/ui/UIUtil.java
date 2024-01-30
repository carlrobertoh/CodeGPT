package ee.carlrobert.codegpt.ui;

import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.toolwindow.chat.ui.SmartScroller;
import java.awt.Dimension;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
    return createTextPane(text, true);
  }

  public static JTextPane createTextPane(String text, boolean opaque) {
    return createTextPane(text, opaque, UIUtil::handleHyperlinkClicked);
  }

  public static JTextPane createTextPane(String text, boolean opaque, HyperlinkListener listener) {
    var textPane = new JTextPane();
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.addHyperlinkListener(listener);
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    textPane.setText(text);
    textPane.setOpaque(opaque);
    return textPane;
  }

  public static JBTextArea createTextArea(String initialValue) {
    var textArea = new JBTextArea(initialValue);
    textArea.setRows(3);
    textArea.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border()),
        JBUI.Borders.empty(4)));
    textArea.setLineWrap(true);
    return textArea;
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

  public static JPanel createRadioButtonsPanel(JBRadioButton... radioButtons) {
    var buttonGroup = new ButtonGroup();
    var radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    for (int i = 0; i < radioButtons.length; i++) {
      JBRadioButton radioButton = radioButtons[i];
      buttonGroup.add(radioButton);
      radioPanel.add(radioButton);
      radioPanel.add(Box.createVerticalStrut(i == radioButtons.length - 1 ? 8 : 4));
    }
    return radioPanel;
  }
}

