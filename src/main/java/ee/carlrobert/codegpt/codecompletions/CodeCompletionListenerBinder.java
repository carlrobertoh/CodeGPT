package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.BulkAwareDocumentListener;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import ee.carlrobert.codegpt.actions.CodeCompletionEnabledListener;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeCompletionListenerBinder implements Disposable {

  private final Editor editor;

  private @Nullable EditorDocumentListener documentListener;
  private @Nullable EditorSelectionListener selectionListener;
  private @Nullable EditorCaretListener caretListener;

  public CodeCompletionListenerBinder(Editor editor) {
    this.editor = editor;

    if (ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled()) {
      addListeners();
    }

    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(
            CodeCompletionEnabledListener.TOPIC,
            (CodeCompletionEnabledListener) (completionsEnabled) -> {
              if (completionsEnabled) {
                addListeners();
                if (editor.getProject() != null) {
                  CodeCompletionService.getInstance(editor.getProject())
                      .handleCompletions(editor, editor.getCaretModel().getOffset());
                }
              } else {
                removeListeners();
              }
            });
  }

  private void addListeners() {
    if (documentListener == null) {
      documentListener = new EditorDocumentListener();
      editor.getDocument().addDocumentListener(documentListener);
    }
    if (selectionListener == null) {
      selectionListener = new EditorSelectionListener();
      editor.getSelectionModel().addSelectionListener(selectionListener);
    }
    if (caretListener == null) {
      caretListener = new EditorCaretListener();
      editor.getCaretModel().addCaretListener(caretListener);
    }
  }

  private void removeListeners() {
    if (documentListener != null) {
      editor.getDocument().removeDocumentListener(documentListener);
      documentListener = null;
    }
    if (selectionListener != null) {
      editor.getSelectionModel().removeSelectionListener(selectionListener);
      selectionListener = null;
    }
    if (caretListener != null) {
      editor.getCaretModel().removeCaretListener(caretListener);
      caretListener = null;
    }
  }

  @Override
  public void dispose() {
    removeListeners();
  }

  private class EditorSelectionListener implements SelectionListener {

    @Override
    public void selectionChanged(@NotNull SelectionEvent event) {
      CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
    }
  }

  private class EditorCaretListener implements CaretListener {

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {
      var project = editor.getProject();
      if (event.getCaret() == null || project == null) {
        return;
      }

      CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
      CodeCompletionService.getInstance(project)
          .handleCompletions(editor, event.getCaret().getOffset());
    }
  }

  private class EditorDocumentListener implements BulkAwareDocumentListener {

    @Override
    public void documentChangedNonBulk(@NotNull DocumentEvent event) {
      var project = editor.getProject();
      if (project != null) {
        CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
        CodeCompletionService.getInstance(project)
            .handleCompletions(editor, editor.getCaretModel().getOffset());
      }
    }
  }
}
