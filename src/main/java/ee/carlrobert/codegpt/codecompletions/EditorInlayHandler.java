package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
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
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.OpenSettingsAction;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.swing.Timer;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

public class EditorInlayHandler implements Disposable {

  private static final Logger LOG = Logger.getInstance(EditorInlayHandler.class);

  private final AtomicReference<EventSource> currentCall = new AtomicReference<>();
  private final CodeCompletionService codeCompletionService = CodeCompletionService.getInstance();
  private final Editor editor;
  private Timer typingTimer;

  public EditorInlayHandler(Editor editor) {
    this.editor = editor;
    initTypingTimer();
    addResetSuggestionListeners();
  }

  @Override
  public void dispose() {
    disableSuggestions();
    removeResetSuggestionListeners();
  }

  private void disableSuggestions() {
    var previousCall = currentCall.get();
    if (previousCall != null) {
      previousCall.cancel();
    }
    disposeInlays();
    typingTimer.stop();
  }

  private void disposeInlays() {
    ActionManager.getInstance().unregisterAction(CodeCompletionService.APPLY_INLAY_ACTION_ID);
    disposeInlay(SINGLE_LINE_INLAY);
    disposeInlay(MULTI_LINE_INLAY);
  }

  private void resetSuggestion() {
    disableSuggestions();
    typingTimer.restart();
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
          var inlayAlreadyExists = Stream.of(SINGLE_LINE_INLAY, MULTI_LINE_INLAY)
              .anyMatch(it -> editor.getUserData(it) != null);
          if (inlayAlreadyExists) {
            return;
          }
          if (!LLAMA_CPP.equals(SettingsState.getInstance().getSelectedService())) {
            return;
          }

          ((Timer) e.getSource()).stop();
          triggerCodeCompletion(editor);
        });
    typingTimer.setRepeats(true);
    typingTimer.start();
  }

  private void triggerCodeCompletion(Editor editor) {
    Project project = editor.getProject();
    Document document = editor.getDocument();

    if (project == null || document.getText().isBlank()) {
      return;
    }

    int caretOffset = editor.getCaretModel().getOffset();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile != null) {
      PsiElement elementAtCaret = ReadAction.compute(() -> psiFile.findElementAt(caretOffset));
      if (codeCompletionService.isCompletionAllowed(elementAtCaret)) {
        var application = ApplicationManager.getApplication();
        final BackgroundableProcessIndicator progressIndicator = new BackgroundableProcessIndicator(
            project, CodeGPTBundle.get("inlineCompletion.progress.title"), null,
            null, true) {
          @Override
          protected void onRunningChange() {
            if (isCanceled()) {
              disableSuggestions();
            }
          }
        };
        application.executeOnPooledThread(() -> {
          var call = codeCompletionService.fetchCodeCompletion(
              elementAtCaret,
              caretOffset,
              editor.getDocument(),
              new CompletionEventListener() {
                @Override
                public void onComplete(StringBuilder messageBuilder) {
                  progressIndicator.processFinish();
                  // If CompletionEventSourceListener is cancelled, onComplete is still called
                  if (progressIndicator.isCanceled()) {
                    return;
                  }
                  var inlayText = messageBuilder.toString();
                  if (!inlayText.isEmpty()) {
                    application.invokeLater(() ->
                        codeCompletionService.addInlays(
                            editor,
                            caretOffset,
                            inlayText,
                            () -> disposeInlays()));
                  }
                }

                @Override
                public void onError(ErrorDetails error, Throwable ex) {
                  progressIndicator.processFinish();
                  LOG.warn(error.getMessage(), ex);
                  Notifications.Bus.notify(OverlayUtil.getDefaultNotification(
                          String.format(
                              CodeGPTBundle.get("notification.completionError.description"),
                              ex.getMessage()),
                          NotificationType.ERROR)
                      .addAction(new OpenSettingsAction()), editor.getProject());
                }
              });
          currentCall.set(call);
          progressIndicator.start();
        });
      }
    }
  }

  private DocumentListener documentListener;
  private KeyAdapter keyListener;
  private EditorMouseListener mouseListener;
  private CaretListener caretListener;
  private SelectionListener selectionListener;

  void addResetSuggestionListeners() {
    removeResetSuggestionListeners();
    documentListener = new DocumentListener() {
      @Override
      public void beforeDocumentChange(@NotNull DocumentEvent event) {
        resetSuggestion();
      }
    };
    editor.getDocument().addDocumentListener(documentListener);

    keyListener = new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        resetSuggestion();
      }

      @Override
      public void keyPressed(KeyEvent e) {
        resetSuggestion();
      }
    };
    editor.getContentComponent().addKeyListener(keyListener);

    mouseListener = new EditorMouseListener() {
      @Override
      public void mouseClicked(@NotNull EditorMouseEvent event) {
        resetSuggestion();
      }
    };
    editor.addEditorMouseListener(mouseListener);

    caretListener = new CaretListener() {
      @Override
      public void caretPositionChanged(@NotNull CaretEvent event) {
        resetSuggestion();
      }
    };
    editor.getCaretModel().addCaretListener(caretListener);

    selectionListener = new SelectionListener() {
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
    };
    editor.getSelectionModel().addSelectionListener(selectionListener);
  }

  private void removeResetSuggestionListeners() {
    if (documentListener != null) {
      editor.getDocument().removeDocumentListener(documentListener);
    }
    if (keyListener != null) {
      editor.getContentComponent().removeKeyListener(keyListener);
    }
    if (mouseListener != null) {
      editor.removeEditorMouseListener(mouseListener);
    }
    if (caretListener != null) {
      editor.getCaretModel().removeCaretListener(caretListener);
    }
    if (selectionListener != null) {
      editor.getSelectionModel().removeSelectionListener(selectionListener);
    }
  }
}
