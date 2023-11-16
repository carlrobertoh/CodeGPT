package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class EditAction extends TrackableAction {

  public EditAction(@NotNull Editor editor) {
    super(
        editor,
        CodeGPTBundle.get("toolwindow.chat.editor.action.edit.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.edit.description"),
        Actions.EditSource,
        ActionType.EDIT_CODE);
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    var editorEx = ((EditorEx) editor);
    editorEx.setViewer(!editorEx.isViewer());

    var viewer = editorEx.isViewer();
    editorEx.setCaretVisible(!viewer);
    editorEx.setCaretEnabled(!viewer);

    var settings = editorEx.getSettings();
    settings.setCaretRowShown(!viewer);

    event.getPresentation().setIcon(viewer ? Actions.EditSource : Actions.Show);
    event.getPresentation().setText(viewer
        ? CodeGPTBundle.get("toolwindow.chat.editor.action.edit.title")
        : CodeGPTBundle.get("toolwindow.chat.editor.action.disableEditing.title"));

    var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
    locationOnScreen.y = locationOnScreen.y - 16;
  }
}
