package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static com.intellij.openapi.application.ActionsKt.runUndoTransparentWriteAction;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import java.awt.Point;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InsertAtCaretAction extends TrackableAction {

  private final @NotNull Editor toolwindowEditor;

  public InsertAtCaretAction(@NotNull Editor toolwindowEditor) {
    super(
        CodeGPTBundle.get("toolwindow.chat.editor.action.insertAtCaret.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.insertAtCaret.description"),
        Icons.SendToTheLeft,
        ActionType.INSERT_AT_CARET);
    this.toolwindowEditor = toolwindowEditor;
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    Point locationOnScreen = getLocationOnScreen(event);
    Editor mainEditor = getSelectedTextEditor();

    if (mainEditor == null) {
      OverlayUtil.showWarningBalloon("Active editor not found", locationOnScreen);
      return;
    }

    insertTextAtCaret(mainEditor);
  }

  @Nullable
  private Point getLocationOnScreen(AnActionEvent event) {
    return Optional.ofNullable(event.getInputEvent())
        .map(inputEvent -> inputEvent.getComponent().getLocationOnScreen())
        .orElse(null);
  }

  @Nullable
  private Editor getSelectedTextEditor() {
    return Optional.ofNullable(toolwindowEditor.getProject())
        .map(FileEditorManager::getInstance)
        .map(FileEditorManager::getSelectedTextEditor)
        .orElse(null);
  }

  private void insertTextAtCaret(Editor mainEditor) {
    runUndoTransparentWriteAction(() -> {
      mainEditor.getDocument().insertString(
          mainEditor.getCaretModel().getOffset(),
          toolwindowEditor.getDocument().getText());
      return null;
    });
  }
}
