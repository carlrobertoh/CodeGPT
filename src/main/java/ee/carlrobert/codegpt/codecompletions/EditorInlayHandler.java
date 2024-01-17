package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;

public class EditorInlayHandler {

  private Timer typingTimer;

  public EditorInlayHandler(Editor editor) {
    initTypingTimer(editor);
    addResetSuggestionListeners(editor);
  }

  public void disableSuggestions(Editor editor) {
    resetSuggestion(editor);
    typingTimer.stop();
  }

  private void resetSuggestion(Editor editor) {
    typingTimer.restart();
    ActionManager actionManager = ActionManager.getInstance();
    actionManager.unregisterAction(CodeCompletionService.INLINE_ELEMENT_ACTION_ID);
    actionManager.unregisterAction(CodeCompletionService.BLOCK_ELEMENT_ACTION_ID);
    disposeInlay(editor, InlayInlineElementRenderer.INLAY_KEY);
    disposeInlay(editor, InlayBlockElementRenderer.INLAY_KEY);
  }

  private void enableSuggestions() {
    if (!typingTimer.isRunning()) {
      typingTimer.start();
    }
  }

  private void disposeInlay(Editor editor, Key<Inlay<EditorCustomElementRenderer>> inlayKey) {
    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(inlayKey);
    if (inlay != null) {
      WriteCommandAction.runWriteCommandAction(editor.getProject(), inlay::dispose);
      editor.putUserData(inlayKey, null);
    }
  }

  private void initTypingTimer(Editor editor) {
    typingTimer = new Timer(
        ConfigurationState.getInstance().getInlineDelay(),
        e -> CodeCompletionService.getInstance()
            .triggerCodeCompletion(editor, () -> typingTimer.stop()));
    typingTimer.setRepeats(true);
    typingTimer.start();
  }

  private void addResetSuggestionListeners(Editor editor) {
    editor.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void beforeDocumentChange(@NotNull DocumentEvent event) {
        resetSuggestion(editor);
      }
    });
    editor.getContentComponent().addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        resetSuggestion(editor);
      }

      @Override
      public void keyPressed(KeyEvent e) {
        resetSuggestion(editor);
      }
    });
    editor.addEditorMouseListener(new EditorMouseListener() {
      @Override
      public void mouseClicked(@NotNull EditorMouseEvent event) {
        resetSuggestion(editor);
      }
    });
    editor.getCaretModel().addCaretListener(new CaretListener() {
      @Override
      public void caretPositionChanged(@NotNull CaretEvent event) {
        resetSuggestion(editor);
      }
    });
    editor.getSelectionModel().addSelectionListener(new SelectionListener() {
      @Override
      public void selectionChanged(@NotNull SelectionEvent e) {
        TextRange range = e.getNewRange();
        int rangeLength = range.getLength();
        if (rangeLength != 0) {
          disableSuggestions(editor);
        } else {
          enableSuggestions();
        }
      }
    });
  }
}
