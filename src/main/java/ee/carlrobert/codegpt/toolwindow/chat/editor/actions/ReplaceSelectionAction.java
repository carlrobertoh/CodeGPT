package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.editor.ex.EditorEx;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplaceSelectionAction extends AbstractAction {

  private final @NotNull EditorEx toolwindowEditor;
  private final Point locationOnScreen;

  public ReplaceSelectionAction(
      @NotNull EditorEx toolwindowEditor,
      @Nullable Point locationOnScreen) {
    super(
        CodeGPTBundle.get("toolwindow.chat.editor.action.replaceSelection.title"),
        Actions.Replace);
    this.toolwindowEditor = toolwindowEditor;
    this.locationOnScreen = locationOnScreen;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    var project = requireNonNull(toolwindowEditor.getProject());
    if (EditorUtil.isMainEditorTextSelected(project)) {
      var mainEditor = EditorUtil.getSelectedEditor(project);
      if (mainEditor != null) {
        EditorUtil.replaceEditorSelection(mainEditor, toolwindowEditor.getDocument().getText());
      }
    } else {
      OverlayUtil.showSelectedEditorSelectionWarning(project, locationOnScreen);
    }
  }
}
