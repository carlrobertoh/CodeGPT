package ee.carlrobert.codegpt.actions.toolwindow;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import org.jetbrains.annotations.NotNull;

public class ReplaceCodeInMainEditorAction extends AnAction {

  public ReplaceCodeInMainEditorAction() {
    super("Replace in Main Editor", "Replace code in main editor", AllIcons.Actions.Replace);
    EditorActionsUtil.registerAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(
        EditorUtil.isMainEditorTextSelected(requireNonNull(event.getProject()))
            && EditorUtil.hasSelection(event.getData(PlatformDataKeys.EDITOR)));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var toolWindowEditor = event.getData(PlatformDataKeys.EDITOR);
    if (project != null && toolWindowEditor != null) {
      var mainEditor = EditorUtil.getSelectedEditor(project);
      if (mainEditor != null) {
        EditorUtil.replaceEditorSelection(
            mainEditor,
            requireNonNull(toolWindowEditor.getSelectionModel().getSelectedText()));
      }
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
