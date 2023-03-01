package ee.carlrobert.chatgpt.ide.action;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.chatgpt.ide.toolwindow.components.SyntaxTextArea;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class CustomPromptDialog extends DialogWrapper {

  private final String selectedText;
  private final String fileExtension;
  private final JTextArea prefixTextArea = new JTextArea();
  private final SyntaxTextArea syntaxTextArea = new SyntaxTextArea(false, false, SyntaxConstants.SYNTAX_STYLE_MARKDOWN);

  public CustomPromptDialog(String selectedText, String fileExtension) {
    super(true);
    this.selectedText = selectedText;
    this.fileExtension = fileExtension;
    setTitle("Custom Prompt");
    setSize(460, 320);
    init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    prefixTextArea.setLineWrap(true);
    prefixTextArea.setWrapStyleWord(true);
    prefixTextArea.setMargin(JBUI.insets(5));

    syntaxTextArea.setText(selectedText.trim());
    // syntaxTextArea.setSyntaxEditingStyle(getSyntaxForFileExtension(fileExtension));
    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

    return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(prefixTextArea)
            .withLabel("Prefix:")
            .moveLabelOnTop()
            .withComment(
                "Example: Find bugs in the following code")
            .createPanel())
        .addVerticalGap(16)
        .addComponent(new JBScrollPane(syntaxTextArea))
        .getPanel();
  }

  public String getPrompt() {
    return prefixTextArea.getText() + "\n\n" + syntaxTextArea.getText();
  }
}

