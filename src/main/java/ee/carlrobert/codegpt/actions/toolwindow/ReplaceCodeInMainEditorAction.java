package ee.carlrobert.codegpt.actions.toolwindow;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.util.EditorUtils;
import org.jetbrains.annotations.NotNull;

public class ReplaceCodeInMainEditorAction extends AnAction {

  public ReplaceCodeInMainEditorAction() {
    super("Replace in Main Editor", "Replace code in main editor", AllIcons.Actions.Replace);
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(
        EditorUtils.isMainEditorTextSelected(requireNonNull(event.getProject()))
            && EditorUtils.hasSelection(event.getData(PlatformDataKeys.EDITOR)));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var toolWindowEditor = event.getData(PlatformDataKeys.EDITOR);
    if (project != null && toolWindowEditor != null) {
      EditorUtils.replaceMainEditorSelection(
          project,
          requireNonNull(toolWindowEditor.getSelectionModel().getSelectedText()));
    }
  }
}
