package ee.carlrobert.codegpt.codecompletions;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
import com.intellij.util.concurrency.annotations.RequiresEdt;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.service.FillInTheMiddle;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.util.EditorUtil;
import java.awt.event.KeyEvent;
import java.util.Optional;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;

@Service
public final class CodeCompletionService {

  public static final String BLOCK_ELEMENT_ACTION_ID = "InsertBlockElementAction";
  public static final String INLINE_ELEMENT_ACTION_ID = "InsertInlineElementAction";

  private static final Logger LOG = Logger.getInstance(CodeCompletionService.class);

  private CodeCompletionService() {
  }

  public static CodeCompletionService getInstance() {
    return ApplicationManager.getApplication().getService(CodeCompletionService.class);
  }

  public void triggerCodeCompletion(@NotNull Editor editor, Runnable stopTimer) {
    Project project = editor.getProject();
    Document document = editor.getDocument();
    if (project == null || document.getText().isBlank()) {
      return;
    }

    int caretOffset = editor.getCaretModel().getOffset();
    PsiFile psiFile =
        PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile != null) {
      var application = ApplicationManager.getApplication();
      application.executeOnPooledThread(() ->
          fetchCodeCompletion(psiFile, editor, caretOffset).ifPresent(completion ->
              application.invokeLater(() -> addInlay(completion, editor, stopTimer))));
    }
  }

  @RequiresWriteLock
  private void applyCompletion(
      @NotNull Editor editor,
      Key<Inlay<EditorCustomElementRenderer>> inlayKey,
      String text) {
    if (editor.isDisposed()) {
      LOG.warn("Editor is already disposed");
      return;
    }

    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(inlayKey);
    if (inlay != null) {
      int offset = inlay.getOffset();
      var document = editor.getDocument();

      document.insertString(offset, text);
      editor.getCaretModel().moveToOffset(offset + text.length());
      EditorUtil.reformatDocument(
          requireNonNull(editor.getProject()),
          document,
          offset,
          offset + text.length());
      inlay.dispose();
      editor.putUserData(inlayKey, null);
    }
  }

  @RequiresBackgroundThread
  private Optional<String> fetchCodeCompletion(PsiFile psiFile, Editor editor, int caretOffset) {
    return ReadAction.compute(() ->
        tryFindPsiMethod(psiFile, caretOffset)
            .map(method -> {
              // TODO: Pass plain string
              return CompletionRequestService.getInstance().getCodeCompletion(new CallParameters(
                  ConversationService.getInstance().startConversation(),
                  ConversationType.INLINE_COMPLETION,
                  createFimMessage(caretOffset, editor.getDocument()),
                  false));
            }));
  }

  @RequiresReadLock
  private Optional<PsiMethod> tryFindPsiMethod(PsiFile psiFile, int caretOffset) {
    PsiElement elementAtCaret = psiFile.findElementAt(caretOffset);
    while (elementAtCaret != null) {
      if (elementAtCaret instanceof PsiMethod) {
        return Optional.of((PsiMethod) elementAtCaret);
      }
      elementAtCaret = elementAtCaret.getParent();
    }
    return Optional.empty();
  }

  private void addInlay(String inlayText, Editor editor, Runnable stopTimer) {
    try {
      var project = editor.getProject();
      var inlayKey = WriteCommandAction.runWriteCommandAction(
          project,
          (Computable<Key<Inlay<EditorCustomElementRenderer>>>) () -> addInlay(inlayText, editor));
      Runnable applyCompletion =
          () -> {
            WriteCommandAction.runWriteCommandAction(project, () ->
                CodeCompletionService.getInstance().applyCompletion(editor, inlayKey, inlayText));
            stopTimer.run();
          };
      if (inlayKey.equals(InlayInlineElementRenderer.INLAY_KEY)) {
        registerInlayAction(INLINE_ELEMENT_ACTION_ID, applyCompletion);
        return;
      }
      registerInlayAction(BLOCK_ELEMENT_ACTION_ID, applyCompletion);
    } finally {
      stopTimer.run();
    }
  }

  @RequiresWriteLock
  private Key<Inlay<EditorCustomElementRenderer>> addInlay(String inlayText, Editor editor) {
    CaretModel caretModel = editor.getCaretModel();
    VisualPosition visualCaretPosition = caretModel.getVisualPosition();
    int caretOffset = editor.visualPositionToOffset(visualCaretPosition);
    return addInlay(inlayText, editor, caretOffset);
  }

  private Key<Inlay<EditorCustomElementRenderer>> addInlay(
      String inlayText,
      Editor editor,
      int offset) {
    var inlayModel = editor.getInlayModel();
    if (inlayText.lines().count() == 1) {
      editor.putUserData(
          InlayInlineElementRenderer.INLAY_KEY,
          inlayModel.addInlineElement(offset, true, new InlayInlineElementRenderer(inlayText)));
      return InlayInlineElementRenderer.INLAY_KEY;
    }

    editor.putUserData(
        InlayBlockElementRenderer.INLAY_KEY,
        inlayModel.addBlockElement(
            offset,
            true,
            true,
            0,
            new InlayBlockElementRenderer(inlayText)));
    return InlayBlockElementRenderer.INLAY_KEY;
  }

  private void registerInlayAction(String actionId, Runnable applyCompletionListener) {
    ActionManager.getInstance().registerAction(
        actionId,
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            ApplicationManager.getApplication().invokeLater(applyCompletionListener);
          }
        });
    KeymapManager.getInstance().getActiveKeymap().addShortcut(
        actionId,
        new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
  }

  private static Message createFimMessage(int offset, Document document) {
    int maxOffset = 4000;
    int begin = Integer.max(0, offset - maxOffset);
    int end = Integer.min(document.getTextLength(), offset + maxOffset);
    var before = document.getText(new TextRange(begin, offset));
    var after = document.getText(new TextRange(offset, end));
    if (SettingsState.getInstance().getSelectedService() == ServiceType.LLAMA_CPP) {
      // Use Messages prompt as input_prefix and response field as input_suffix
      return new Message(before, after);
    }
    FillInTheMiddle fim = SettingsState.getInstance().getSelectedService().getFillInTheMiddle();
    return new Message(
        ConfigurationState.getInstance().getInlineCompletionPrompt()
            .replace("{pre}", fim.getPrefix())
            .replace("{codeBefore}", before)
            .replace("{suf}", fim.getSuffix())
            .replace("{codeAfter}", after)
            .replace("{mid}", fim.getMiddle()));
  }
}
