package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
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
    if (EditorUtil.isMainEditorTextSelected(project)) {
      var mainEditor = EditorUtil.getSelectedEditor(project);
      if (mainEditor != null) {
        EditorUtil.replaceEditorSelection(mainEditor, editor.getDocument().getText());
      }
    } else {
      OverlayUtil.showSelectedEditorSelectionWarning(event);
    }
  }
}
