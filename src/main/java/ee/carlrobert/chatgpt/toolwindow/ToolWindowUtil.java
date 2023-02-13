package ee.carlrobert.chatgpt.toolwindow;

import com.intellij.ui.JBColor;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ToolWindowUtil {

  public static JTextArea createTextArea(String selectedText, boolean isItalicFont, boolean transparentBackground) {
    var textArea = new JTextArea();
    textArea.append(selectedText);
    textArea.setLineWrap(true);
    textArea.setEditable(false);
    textArea.setFont(createFont(isItalicFont, textArea.getFont().getSize()));
    textArea.setWrapStyleWord(true);
    if (transparentBackground) {
      textArea.setBackground(JBColor.background());
    }
    // textArea.setBorder(new MatteBorder(0, 2, 0, 0, JBColor.RED));
    return textArea;
  }

  public static JLabel createIconLabel(URL iconLocation) {
    var iconLabel = new JLabel(new ImageIcon(iconLocation));
    iconLabel.setText("ChatGPT");
    iconLabel.setFont(iconLabel.getFont().deriveFont(iconLabel.getFont().getStyle() | Font.BOLD));
    iconLabel.setIconTextGap(10);
    return iconLabel;
  }

  public static Font createFont(boolean isItalic, int fontSize) {
    return new Font("Tahoma", isItalic ? Font.ITALIC : Font.PLAIN, fontSize);
  }

  public static Box justifyLeft(Component component) {
    Box box = Box.createHorizontalBox();
    box.add(component);
    box.add(Box.createHorizontalGlue());
    return box;
  }
}

