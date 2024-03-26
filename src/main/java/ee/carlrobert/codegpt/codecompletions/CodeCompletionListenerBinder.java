package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.BulkAwareDocumentListener;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import ee.carlrobert.codegpt.actions.CodeCompletionEnabledListener;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import java.util.List;
import javax.swing.SwingUtilities;
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
        .connect(this)
        .subscribe(
            CodeCompletionEnabledListener.TOPIC,
            (CodeCompletionEnabledListener) (completionsEnabled) -> {
              if (completionsEnabled) {
                addListeners();
              } else {
                removeListeners();
              }
            });
  }

  public void addListeners() {
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

  public void removeListeners() {
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
      if (!"Typing".equals(CommandProcessor.getInstance().getCurrentCommandName())) {
        CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
        if (editor.getProject() != null) {
          CodeCompletionService.getInstance(editor.getProject()).cancelPreviousCall();
        }
      }
    }
  }

  private class EditorDocumentListener implements BulkAwareDocumentListener {

    @Override
    public void documentChangedNonBulk(@NotNull DocumentEvent event) {
      ApplicationManager.getApplication().executeOnPooledThread(() -> {
        CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);

        var commandName = CommandProcessor.getInstance().getCurrentCommandName();
        if (CommandProcessor.getInstance().isUndoTransparentActionInProgress()
            || isCommandExcluded(commandName)) {
          return;
        }

        var project = editor.getProject();
        if (project != null) {
          var codeCompletionService = CodeCompletionService.getInstance(project);
          SwingUtilities.invokeLater(() -> {
            var caretOffset = editor.getCaretModel().getOffset();
            var charTyped = event.getNewFragment().toString().trim();

            // cancel previous completions when typed.
            codeCompletionService.cancelPreviousCall();

            if (shouldTriggerCodeCompletion(charTyped)) {
              codeCompletionService.triggerCompletions(editor, caretOffset, CodeCompletionTriggerType.AUTOMATIC);
            }
          });
        }
      });
    }

    private boolean shouldTriggerCodeCompletion(String charTyped) {
      if (charTyped.isEmpty()) {
        return true;
      }
      return PREVIOUS_INLAY_TEXT.get(editor) == null;
    }

    private boolean isCommandExcluded(String commandName) {
      return commandName != null
          && List.of("Up", "Down", "Left", "Right", "Move Caret").contains(commandName);
    }
  }
}
