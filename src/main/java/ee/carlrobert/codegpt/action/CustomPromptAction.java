package ee.carlrobert.codegpt.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.util.FileUtils;
import javax.swing.SwingUtilities;

public class CustomPromptAction extends BaseAction {

  public CustomPromptAction() {
    super("Custom Prompt", "Custom prompt description", AllIcons.Actions.Run_anything);
  }

  private static String previousUserPrompt = "";

  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    if (selectedText != null && !selectedText.isEmpty()) {
      var fileExtension = FileUtils.getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
      var dialog = new CustomPromptDialog(selectedText, fileExtension, previousUserPrompt);
      if (dialog.showAndGet()) {
        previousUserPrompt = dialog.getUserPrompt();
        SwingUtilities.invokeLater(() -> sendMessage(project, dialog.getFullPrompt()));
      }
    }
  }
}
