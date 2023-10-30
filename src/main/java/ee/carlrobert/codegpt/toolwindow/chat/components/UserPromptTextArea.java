package ee.carlrobert.codegpt.toolwindow.chat.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserPromptTextArea extends JPanel {

  private static final String TEXT_SUBMIT = "text-submit";
  private static final String INSERT_BREAK = "insert-break";
  private static final JBColor BACKGROUND_COLOR = JBColor.namedColor("Editor.SearchField.background", UIUtil.getTextFieldBackground());

  private final JBTextArea textArea;

  private final int textAreaRadius = 16;
  private final Consumer<String> onSubmit;
  private JButton stopButton;
  private JPanel iconsPanel;
  private boolean submitEnabled = true;

  public UserPromptTextArea(Consumer<String> onSubmit) {
    this.onSubmit = onSubmit;

    textArea = new JBTextArea();
    textArea.setOpaque(false);
    textArea.setBackground(BACKGROUND_COLOR);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.getEmptyText().setText("Ask me anything");
    textArea.setBorder(JBUI.Borders.empty(8, 4));
    var input = textArea.getInputMap();
    input.put(KeyStroke.getKeyStroke("ENTER"), TEXT_SUBMIT);
    input.put(KeyStroke.getKeyStroke("shift ENTER"), INSERT_BREAK);
    textArea.getActionMap().put(TEXT_SUBMIT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleSubmit();
      }
    });
    textArea.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
      }

      @Override
      public void focusLost(FocusEvent e) {
        UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
      }
    });
    textArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        if (e.getDocument().getLength() == 0) {
          iconsPanel.getComponents()[0].setEnabled(false);
        }
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        if (e.getDocument().getLength() == 1) {
          iconsPanel.getComponents()[0].setEnabled(true);
        }
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
    updateFont();
    init();
  }

  public void focus() {
    textArea.requestFocus();
    textArea.requestFocusInWindow();
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
    g2.setColor(JBUI.CurrentTheme.ActionButton.focusedBorder());
    if (textArea.isFocusOwner()) {
      g2.setStroke(new BasicStroke(1.5F));
    }
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
  }

  @Override
  public Insets getInsets() {
    return JBUI.insets(6, 12, 6, 6);
  }

  public void setSubmitEnabled(boolean submitEnabled) {
    this.submitEnabled = submitEnabled;
    stopButton.setEnabled(!submitEnabled);
  }

  public void setTextAreaEnabled(boolean textAreaEnabled) {
    textArea.setEnabled(textAreaEnabled);
  }

  private void handleSubmit() {
    if (submitEnabled && !textArea.getText().isEmpty()) {
      // Replacing each newline with two newlines to ensure proper Markdown formatting
      var text = textArea.getText().replace("\n", "\n\n");
      onSubmit.accept(text);
      textArea.setText("");
    }
  }

  private void init() {
    setOpaque(false);
    setLayout(new BorderLayout());
    add(textArea, BorderLayout.CENTER);

    stopButton = createIconButton(AllIcons.Actions.Suspend, null);

    var flowLayout = new FlowLayout(FlowLayout.RIGHT);
    flowLayout.setHgap(8);
    iconsPanel = new JPanel(flowLayout);
    iconsPanel.add(createIconButton(Icons.SendIcon, this::handleSubmit));
    iconsPanel.add(stopButton);
    add(JBUI.Panels.simplePanel().addToBottom(iconsPanel), BorderLayout.EAST);
  }

  private void updateFont() {
    if (Registry.is("ide.find.use.editor.font", false)) {
      textArea.setFont(EditorUtil.getEditorFont());
    } else {
      textArea.setFont(UIManager.getFont("TextField.font"));
    }
  }

  // TODO: IconActionButton?
  private JButton createIconButton(Icon icon, @Nullable Runnable submitListener) {
    var button = SwingUtils.createIconButton(icon);
    if (submitListener != null) {
      button.addActionListener((e) -> handleSubmit());
    }
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setEnabled(false);
    return button;
  }

  public void setRequestHandler(@NotNull CompletionRequestHandler requestService) {
    stopButton.addActionListener(e -> requestService.cancel());
  }
}
