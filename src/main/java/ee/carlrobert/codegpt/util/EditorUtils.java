package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.testFramework.LightVirtualFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EditorUtils {

  public static Editor createEditor(@NotNull Project project, String fileExtension, String code) {
    var timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    var fileName = "temp_" + timestamp + fileExtension;
    var lightVirtualFile = new LightVirtualFile(
        format("%s/%s", PathManager.getTempPath(), fileName),
        code);
    var existingDocument = FileDocumentManager.getInstance().getDocument(lightVirtualFile);
    var document = existingDocument != null
        ? existingDocument
        : EditorFactory.getInstance().createDocument(code);

    disableHighlighting(project, document);

    return EditorFactory.getInstance().createEditor(
        document,
        project,
        lightVirtualFile,
        false,
        EditorKind.MAIN_EDITOR);
  }

  public static boolean hasSelection(@Nullable Editor editor) {
    return editor != null && editor.getSelectionModel().hasSelection();
  }

  public static @Nullable Editor getSelectedEditor(@NotNull Project project) {
    FileEditorManager editorManager = FileEditorManager.getInstance(project);
    return editorManager != null ? editorManager.getSelectedTextEditor() : null;
  }

  public static @Nullable String getSelectedEditorSelectedText(@NotNull Project project) {
    var selectedEditor = EditorUtils.getSelectedEditor(project);
    if (selectedEditor != null) {
      return selectedEditor.getSelectionModel().getSelectedText();
    }
    return null;
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
            editor.getDocument()
                .replaceString(
                    selectionModel.getSelectionStart(),
                    selectionModel.getSelectionEnd(),
                    text);
            editor.getContentComponent().requestFocus();
            selectionModel.removeSelection();
          }
        })));
  }

  public static void disableHighlighting(@NotNull Project project, Document document) {
    var psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile != null) {
      DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
    }
  }
}
