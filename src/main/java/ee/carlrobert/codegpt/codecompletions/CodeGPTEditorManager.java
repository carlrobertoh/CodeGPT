package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;

@Service
public final class CodeGPTEditorManager {
  private CodeGPTEditorManager() {
  }

  public static CodeGPTEditorManager getInstance() {
    return ApplicationManager.getApplication().getService(CodeGPTEditorManager.class);
  }

  public void disposeAllInlays(Project project) {
    var allFileEditors = FileEditorManager.getInstance(project).getAllEditors();
    for (FileEditor fileEditor : allFileEditors) {
      if (fileEditor instanceof TextEditor) {
        disposeEditorInlays(((TextEditor) fileEditor).getEditor());
      }
    }
  }

  public void disposeEditorInlays(Editor editor) {
    disposeInlay(editor, SINGLE_LINE_INLAY);
    disposeInlay(editor, MULTI_LINE_INLAY);
  }

  private void disposeInlay(Editor editor, Key<Inlay<EditorCustomElementRenderer>> inlayKey) {
    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(inlayKey);
    if (inlay != null) {
      WriteCommandAction.runWriteCommandAction(editor.getProject(), inlay::dispose);
      editor.putUserData(inlayKey, null);
    }
  }
}
