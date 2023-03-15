package ee.carlrobert.codegpt.ide.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

public class CustomPromptAction extends BaseAction {

  public CustomPromptAction() {
    super("Custom Prompt", "Custom prompt description", AllIcons.Actions.Run_anything);
  }

  private static String previousUserPrompt = "";

  protected void actionPerformed(Project project, Editor editor, String selectedText) {
    if (selectedText != null && !selectedText.isEmpty()) {
      var fileExtension = getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
      var dialog = new CustomPromptDialog(selectedText, fileExtension, previousUserPrompt);
      if (dialog.showAndGet()) {
        previousUserPrompt = dialog.getUserPrompt();
        SwingUtilities.invokeLater(() -> sendMessage(project, dialog.getFullPrompt()));
      }
    }
  }

  private String getFileExtension(String filename) {
    Pattern pattern = Pattern.compile("[^.]+$");
    Matcher matcher = pattern.matcher(filename);

    if (matcher.find()) {
      return matcher.group();
    }
    return null;
  }
}
