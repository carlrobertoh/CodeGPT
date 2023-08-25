package ee.carlrobert.codegpt.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EditorUtils {

  public static boolean hasSelection(@Nullable Editor editor) {
    return editor != null && editor.getSelectionModel().hasSelection();
  }

  public static @Nullable Editor getSelectedEditor(@NotNull Project project) {
    FileEditorManager editorManager = FileEditorManager.getInstance(project);
    return editorManager != null ? editorManager.getSelectedTextEditor() : null;
  }

  public static boolean isMainEditorTextSelected(@NotNull Project project) {
    return hasSelection(getSelectedEditor(project));
  }

  public static void replaceMainEditorSelection(@NotNull Project project, @NotNull String text) {
    var application = ApplicationManager.getApplication();
    application.invokeLater(() ->
        application.runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
          var editor = getSelectedEditor(project);
          if (editor != null) {
            var selectionModel = editor.getSelectionModel();
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), text);
            editor.getContentComponent().requestFocus();
            selectionModel.removeSelection();
          }
        })));
  }
}
