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
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Pair;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import org.jetbrains.annotations.NotNull;

public class DiffAction extends TrackableAction {

  public DiffAction(@NotNull Editor editor) {
    super(
        editor,
        CodeGPTBundle.get("toolwindow.chat.editor.action.diff.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.diff.description"),
        Actions.DiffWithClipboard,
        ActionType.DIFF_CODE);
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    var project = requireNonNull(event.getProject());
    var selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (!EditorUtils.hasSelection(selectedTextEditor)) {
      OverlayUtils.showSelectedEditorSelectionWarning(event);
      return;
    }

    var resultEditorFile = FileUtils.getEditorFile(selectedTextEditor);
    var diffContentFactory = DiffContentFactory.getInstance();
    var request = new SimpleDiffRequest(
        CodeGPTBundle.get("editor.diff.title"),
        diffContentFactory.create(project, FileUtils.getEditorFile(editor)),
        diffContentFactory.create(project, resultEditorFile),
        CodeGPTBundle.get("editor.diff.local.content.title"),
        resultEditorFile.getName());
    request.putUserData(
        DiffUserDataKeys.SCROLL_TO_LINE,
        Pair.create(Side.RIGHT, DiffUtil.getCaretPosition(selectedTextEditor).line));

    DiffManager.getInstance().showDiff(project, request, DiffDialogHints.DEFAULT);
  }
}
