package ee.carlrobert.chatgpt.ide.toolwindow;

import com.intellij.ui.JBColor;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ToolWindowUtil {

  public static JTextArea createTextArea(String selectedText, boolean isItalicFont) {
    var textArea = new JTextArea();
    textArea.append(selectedText);
    textArea.setLineWrap(true);
    textArea.setEditable(false);
    textArea.setFont(new Font("Tahoma", isItalicFont ? Font.ITALIC : Font.PLAIN, textArea.getFont().getSize()));
    textArea.setWrapStyleWord(true);
    textArea.setBackground(JBColor.PanelBackground);
    // textArea.setBorder(new MatteBorder(0, 2, 0, 0, JBColor.RED));
    return textArea;
  }

  public static JLabel createIconLabel(URL iconLocation, String text) {
    var iconLabel = new JLabel(new ImageIcon(iconLocation));
    iconLabel.setText(text);
    iconLabel.setFont(iconLabel.getFont().deriveFont(iconLabel.getFont().getStyle() | Font.BOLD));
    iconLabel.setIconTextGap(8);
    return iconLabel;
  }

  public static Box justifyLeft(Component component) {
    Box box = Box.createHorizontalBox();
    box.add(component);
    box.add(Box.createHorizontalGlue());
    return box;
  }
}

