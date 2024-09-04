package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.EditorDiffUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jetbrains.annotations.Nullable;

public class DiffAction extends AbstractAction {

  private final EditorEx toolwindowEditor;
  private final Point locationOnScreen;

  public DiffAction(EditorEx toolwindowEditor, @Nullable Point locationOnScreen) {
    super("Diff", Actions.DiffWithClipboard);
    this.toolwindowEditor = toolwindowEditor;
    this.locationOnScreen = locationOnScreen;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    var project = requireNonNull(toolwindowEditor.getProject());
    var mainEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (mainEditor != null && !EditorUtil.hasSelection(mainEditor) && locationOnScreen != null) {
      OverlayUtil.showSelectedEditorSelectionWarning(project, locationOnScreen);
      return;
    }

    EditorDiffUtil.showDiff(
        project,
        toolwindowEditor,
        mainEditor.getSelectionModel().getSelectedText());
  }
}
