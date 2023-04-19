package ee.carlrobert.codegpt.action;

import static ee.carlrobert.codegpt.util.SwingUtils.addShiftEnterInputMap;
import static java.lang.String.format;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JTextArea;

public class CustomPromptDialog extends DialogWrapper {

  private final String selectedText;
  private final String fileExtension;
  private final JTextArea userPromptTextArea;

  public CustomPromptDialog(String selectedText, String fileExtension, String previousUserPrompt) {
    super(true);
    this.selectedText = selectedText;
    this.fileExtension = fileExtension;
    this.userPromptTextArea = new JTextArea(previousUserPrompt);
    this.userPromptTextArea.setCaretPosition(previousUserPrompt.length());
    setTitle("Custom Prompt");
    setSize(400, getRootPane().getPreferredSize().height);
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

    return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(userPromptTextArea)
            .withLabel("Prefix:")
            .moveLabelOnTop()
            .withComment(
                "Example: Find bugs in the following code")
            .createPanel())
        .getPanel();
  }

  public String getFullPrompt() {
    return userPromptTextArea.getText() + format("\n```%s\n%s\n```", fileExtension, selectedText);
  }

  public String getUserPrompt() {
    return userPromptTextArea.getText();
  }
}

