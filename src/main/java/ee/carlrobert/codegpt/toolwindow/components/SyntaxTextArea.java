package ee.carlrobert.codegpt.toolwindow.components;

import static ee.carlrobert.codegpt.util.SwingUtils.createIconButton;

import com.intellij.icons.AllIcons;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import javax.swing.JButton;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.CaretStyle;

public class SyntaxTextArea extends RSyntaxTextArea {

  public SyntaxTextArea(boolean isReadOnly, boolean withBlockCaret, String syntax) {
    super("");
    setStyles(isReadOnly, withBlockCaret, syntax);
  }

  public void displayCopyButton() {
    ComponentBorder cb = new ComponentBorder(createCopyButton());
    cb.setAlignment(TOP_ALIGNMENT);
    cb.install(this);
  }

  public void hideCaret() {
    getCaret().setVisible(false);
  }

  public void changeStyleViaThemeXml() {
    var baseThemePath = "/org/fife/ui/rsyntaxtextarea/themes/";
    try {
      Theme theme = Theme.load(getClass().getResourceAsStream(
          UIUtil.isUnderDarcula() ? baseThemePath + "dark.xml" : baseThemePath + "idea.xml"));
      theme.baseFont = JBFont.regular();
      theme.apply(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setStyles(boolean isReadOnly, boolean withBlockCaret, String syntax) {
    setMargin(JBUI.insets(5));
    setAntiAliasingEnabled(true);
    setEnabled(true);
    setEditable(!isReadOnly);
    setPaintTabLines(false);
    setHighlightCurrentLine(false);
    setLineWrap(true);
    if (withBlockCaret) {
      setCaretStyle(0, CaretStyle.BLOCK_STYLE);
      getCaret().setVisible(true);
    }
    setSyntaxEditingStyle(syntax);
    changeStyleViaThemeXml();
  }

  private void copyToClipboard() {
    StringSelection stringSelection = new StringSelection(getText().trim());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }

  private JButton createCopyButton() {
    var button = createIconButton(AllIcons.General.InlineCopy);
    button.addActionListener(e -> {
      copyToClipboard();
      button.setIcon(AllIcons.General.InspectionsOK);
    });
    return button;
  }
}
