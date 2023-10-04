package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import org.jetbrains.annotations.NotNull;

public class ReplaceSelectionAction extends TrackableAction {

  public ReplaceSelectionAction(@NotNull Editor editor) {
    super(
        editor,
        CodeGPTBundle.get("toolwindow.chat.editor.action.replaceSelection.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.replaceSelection.description"),
        Actions.Replace,
        ActionType.REPLACE_IN_MAIN_EDITOR);
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    var project = requireNonNull(event.getProject());
    if (EditorUtils.isMainEditorTextSelected(project)) {
      EditorUtils.replaceMainEditorSelection(project, editor.getDocument().getText());
    } else {
      OverlayUtils.showSelectedEditorSelectionWarning(event);
    }
  }
}
