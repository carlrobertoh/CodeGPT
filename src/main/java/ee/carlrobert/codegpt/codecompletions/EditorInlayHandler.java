package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;

import com.intellij.openapi.Disposable;
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
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;

public class EditorInlayHandler implements Disposable {

  private final Editor editor;
  private Timer typingTimer;

  public EditorInlayHandler(Editor editor) {
    this.editor = editor;
    initTypingTimer();
    addResetSuggestionListeners();
  }

  @Override
  public void dispose() {
    ActionManager.getInstance().unregisterAction(CodeCompletionService.APPLY_INLAY_ACTION_ID);
    disposeInlay(SINGLE_LINE_INLAY);
    disposeInlay(CodeGPTKeys.MULTI_LINE_INLAY);
  }

  private void disableSuggestions() {
    resetSuggestion();
    typingTimer.stop();
  }

  private void resetSuggestion() {
    typingTimer.restart();
    dispose();
  }

  private void enableSuggestions() {
    if (!typingTimer.isRunning()) {
      typingTimer.start();
    }
  }

  private void disposeInlay(Key<Inlay<EditorCustomElementRenderer>> inlayKey) {
    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(inlayKey);
    if (inlay != null) {
      WriteCommandAction.runWriteCommandAction(editor.getProject(), inlay::dispose);
      editor.putUserData(inlayKey, null);
    }
  }

  private void initTypingTimer() {
    typingTimer = new Timer(
        ConfigurationState.getInstance().getInlineDelay(),
        e -> {
          if (Stream.of(SINGLE_LINE_INLAY, MULTI_LINE_INLAY)
              .anyMatch(it -> editor.getUserData(it) != null)) {
            return;
          }

          ((Timer) e.getSource()).stop();
          CodeCompletionService.getInstance().triggerCodeCompletion(editor);
        });
    typingTimer.setRepeats(true);
    typingTimer.start();
  }

  private void addResetSuggestionListeners() {
    editor.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void beforeDocumentChange(@NotNull DocumentEvent event) {
        resetSuggestion();
      }
    });
    editor.getContentComponent().addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        resetSuggestion();
      }

      @Override
      public void keyPressed(KeyEvent e) {
        resetSuggestion();
      }
    });
    editor.addEditorMouseListener(new EditorMouseListener() {
      @Override
      public void mouseClicked(@NotNull EditorMouseEvent event) {
        resetSuggestion();
      }
    });
    editor.getCaretModel().addCaretListener(new CaretListener() {
      @Override
      public void caretPositionChanged(@NotNull CaretEvent event) {
        resetSuggestion();
      }
    });
    editor.getSelectionModel().addSelectionListener(new SelectionListener() {
      @Override
      public void selectionChanged(@NotNull SelectionEvent e) {
        TextRange range = e.getNewRange();
        int rangeLength = range.getLength();
        if (rangeLength != 0) {
          disableSuggestions();
        } else {
          enableSuggestions();
        }
      }
    });
  }
}
