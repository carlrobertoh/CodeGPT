package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUtil;
import com.intellij.diff.util.Side;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Pair;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jetbrains.annotations.NotNull;

public class DiffAction extends AbstractAction {

  private final EditorEx editor;
  private final Point locationOnScreen;

  public DiffAction(@NotNull EditorEx editor, @NotNull Point locationOnScreen) {
    super("Diff", Actions.DiffWithClipboard);
    this.editor = editor;
    this.locationOnScreen = locationOnScreen;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    var project = requireNonNull(editor.getProject());
    var selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (!EditorUtil.hasSelection(selectedTextEditor)) {
      OverlayUtil.showSelectedEditorSelectionWarning(project, locationOnScreen);
      return;
    }

    var resultEditorFile = FileUtil.getEditorFile(selectedTextEditor);
    var diffContentFactory = DiffContentFactory.getInstance();
    var request = new SimpleDiffRequest(
        CodeGPTBundle.get("editor.diff.title"),
        diffContentFactory.create(project, FileUtil.getEditorFile(editor)),
        diffContentFactory.create(project, resultEditorFile),
        CodeGPTBundle.get("editor.diff.local.content.title"),
        resultEditorFile.getName());
    request.putUserData(
        DiffUserDataKeys.SCROLL_TO_LINE,
        Pair.create(Side.RIGHT, DiffUtil.getCaretPosition(selectedTextEditor).line));

    DiffManager.getInstance().showDiff(project, request, DiffDialogHints.DEFAULT);
  }
}
