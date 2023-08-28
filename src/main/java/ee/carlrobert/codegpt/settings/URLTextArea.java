package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.settings.state.CustomSettingsState;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

public class URLTextArea extends JPanel {

  private static final String TEXT_SUBMIT = "text-submit";
  private static final String INSERT_BREAK = "insert-break";
  private static final JBColor BACKGROUND_COLOR = JBColor.namedColor("Editor.SearchField.background", UIUtil.getTextFieldBackground());

  private final JBTextArea textArea;

  private final int textAreaRadius = 16;

  public URLTextArea(String text, boolean enabled) {
    textArea = new JBTextArea(text);
    textArea.setOpaque(false);
    textArea.setEnabled(enabled);
    textArea.setBackground(BACKGROUND_COLOR);
    textArea.setLineWrap(true);
    textArea.getEmptyText().setText("Enter URL or paste text");
    textArea.setBorder(JBUI.Borders.empty(4, 8, 4, 4));
    var input = textArea.getInputMap();
    input.put(KeyStroke.getKeyStroke("ENTER"), TEXT_SUBMIT);
    input.put(KeyStroke.getKeyStroke("shift ENTER"), INSERT_BREAK);
    textArea.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        URLTextArea.super.paintBorder(URLTextArea.super.getGraphics());
      }

      @Override
      public void focusLost(FocusEvent e) {
        URLTextArea.super.paintBorder(URLTextArea.super.getGraphics());
      }
    });
    updateFont();
    init(enabled);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
    super.paintComponent(g);
  }

  @Override
  protected void paintBorder(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground());
    if (textArea.isFocusOwner()) {
      g2.setStroke(new BasicStroke(1.5F));
    }
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
  }

  @Override
  public Insets getInsets() {
    return JBUI.insets(6, 12, 6, 6);
  }

  public void setText(String text) {
    textArea.setText(text);
  }

  private void init(boolean enabled) {
    setOpaque(false);
    setLayout(new BorderLayout());

    var comboboxModel = new DefaultComboBoxModel<String>();
    comboboxModel.addElement("POST");
    comboboxModel.addElement("GET");
    comboboxModel.setSelectedItem(CustomSettingsState.getInstance().getHttpMethod());
    var combobox = new ComboBox<>(comboboxModel);
    combobox.setEnabled(enabled);
    combobox.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground(), 0, 0, 0, 1),
        JBUI.Borders.empty()));

    add(JBUI.Panels.simplePanel().addToBottom(combobox), BorderLayout.WEST);
    add(textArea, BorderLayout.CENTER);
  }

  private void updateFont() {
    if (Registry.is("ide.find.use.editor.font", false)) {
      textArea.setFont(EditorUtil.getEditorFont());
    } else {
      textArea.setFont(UIManager.getFont("TextField.font"));
    }
  }
}
