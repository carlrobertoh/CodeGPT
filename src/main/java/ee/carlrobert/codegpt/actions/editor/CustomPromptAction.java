package ee.carlrobert.codegpt.actions.editor;

import static java.lang.String.format;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;

class CustomPromptAction extends BaseEditorAction {

  private static String previousUserPrompt = "";

  CustomPromptAction() {
    super("Custom Prompt", "Custom prompt description", AllIcons.Actions.Run_anything);
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    if (selectedText != null && !selectedText.isEmpty()) {
      var fileExtension =
          FileUtils.getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
      var dialog = new CustomPromptDialog(previousUserPrompt);
      if (dialog.showAndGet()) {
        previousUserPrompt = dialog.getUserPrompt();
        var message = new Message(
            format("%s\n```%s\n%s\n```", previousUserPrompt, fileExtension, selectedText));
        message.setUserMessage(previousUserPrompt);
        SwingUtilities.invokeLater(() ->
            project.getService(StandardChatToolWindowContentManager.class).sendMessage(message));
      }
    }
  }

  private static class CustomPromptDialog extends DialogWrapper {

    private final JTextArea userPromptTextArea;

    public CustomPromptDialog(String previousUserPrompt) {
      super(true);
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
      SwingUtils.addShiftEnterInputMap(userPromptTextArea, this::clickDefaultButton);

      return FormBuilder.createFormBuilder()
          .addComponent(UI.PanelFactory.panel(userPromptTextArea)
              .withLabel("Prefix:")
              .moveLabelOnTop()
              .withComment("Example: Find bugs in the following code")
              .createPanel())
          .getPanel();
    }

    public String getUserPrompt() {
      return userPromptTextArea.getText();
    }
  }
}