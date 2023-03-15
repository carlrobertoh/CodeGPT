package ee.carlrobert.codegpt.ide.action;

import static ee.carlrobert.codegpt.ide.util.SwingUtils.addShiftEnterInputMap;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.ide.toolwindow.components.SyntaxTextArea;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class CustomPromptDialog extends DialogWrapper {

  private final String selectedText;
  private final String fileExtension;
  private final JTextArea userPromptTextArea;
  private final SyntaxTextArea syntaxTextArea =
      new SyntaxTextArea(false, false, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);

  public CustomPromptDialog(String selectedText, String fileExtension, String previousUserPrompt) {
    super(true);
    this.selectedText = selectedText;
    this.fileExtension = fileExtension;
    this.userPromptTextArea = new JTextArea(previousUserPrompt);
    this.userPromptTextArea.setCaretPosition(previousUserPrompt.length());
    setTitle("Custom Prompt");
    setSize(460, 320);
    init();
  }

  @Nullable
  public JComponent getPreferredFocusedComponent() {
    return userPromptTextArea;
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    userPromptTextArea.setLineWrap(true);
    userPromptTextArea.setWrapStyleWord(true);
    userPromptTextArea.setMargin(JBUI.insets(5));
    addShiftEnterInputMap(userPromptTextArea, this::clickDefaultButton);

    syntaxTextArea.setText(selectedText.trim());
    // syntaxTextArea.setSyntaxEditingStyle(getSyntaxForFileExtension(fileExtension));
    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

    return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(userPromptTextArea)
            .withLabel("Prefix:")
            .moveLabelOnTop()
            .withComment(
                "Example: Find bugs in the following code")
            .createPanel())
        .addVerticalGap(16)
        .addComponent(new JBScrollPane(syntaxTextArea))
        .getPanel();
  }

  public String getFullPrompt() {
    return userPromptTextArea.getText() + "\n\n" + syntaxTextArea.getText();
  }

  public String getUserPrompt() {
    return userPromptTextArea.getText();
  }
}

