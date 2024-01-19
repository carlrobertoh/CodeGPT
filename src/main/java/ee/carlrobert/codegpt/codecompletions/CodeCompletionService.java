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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.util.EditorUtil;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;

@Service
@ParametersAreNonnullByDefault
public final class CodeCompletionService {

  public static final String BLOCK_ELEMENT_ACTION_ID = "InsertBlockElementAction";
  public static final String INLINE_ELEMENT_ACTION_ID = "InsertInlineElementAction";
  public static final int MAX_OFFSET = 4000;

  private static final Logger LOG = Logger.getInstance(CodeCompletionService.class);

  private CodeCompletionService() {
  }

  public static CodeCompletionService getInstance() {
    return ApplicationManager.getApplication().getService(CodeCompletionService.class);
  }

  public void triggerCodeCompletion(Editor editor, Runnable stopTimer) {
    Project project = editor.getProject();
    Document document = editor.getDocument();
    if (project == null || document.getText().isBlank()) {
      return;
    }

    int caretOffset = editor.getCaretModel().getOffset();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile != null) {
      // TODO: Should cancel current completion request, if present
      stopTimer.run();

      ApplicationManager.getApplication().executeOnPooledThread(() -> {
        var response = fetchCodeCompletion(psiFile, caretOffset, editor.getDocument());
        addInlay(response, editor, stopTimer);
      });
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

  /**
   * Fetches code-completion (FIM) for the given position ({@code offsetInFile}) in the file. <br/>
   * By default tries to find an enclosing {@link PsiMethod} or {@link PsiClass} for the given
   * {@code offsetInFile} and only uses their content instead of the entire file's content. If no
   * such enclosing {@link PsiElement} can be found, the file's entire content is used instead.
   *
   * @param psiFile      File to determine what text content to use.
   * @param offsetInFile Global offset in the file.
   * @param document     If the offset is not enclosed in a {@link PsiMethod} nor a
   *                     {@link PsiClass}, the entire file content is used for completion.
   * @return Completion String
   */
  @RequiresBackgroundThread
  private String fetchCodeCompletion(PsiFile psiFile, int offsetInFile, Document document) {
    InfillRequestDetails requestDetails = ReadAction.compute(() -> tryFindEnclosingPsiElement(
        List.of(PsiMethod.class, PsiClass.class),
        psiFile, offsetInFile)
        .map(psiElement -> {
          var textRange = psiElement.getTextRange();
          return createInfillRequest(
              document,
              offsetInFile,
              textRange.getStartOffset(),
              textRange.getEndOffset());
        })
        .orElse(createInfillRequest(offsetInFile, document)));
    return CompletionRequestService.getInstance().getCodeCompletion(requestDetails);
  }

  @RequiresReadLock
  private Optional<PsiElement> tryFindEnclosingPsiElement(
      List<Class<? extends PsiElement>> types,
      PsiFile psiFile, int caretOffset) {
    PsiElement elementAtCaret = psiFile.findElementAt(caretOffset);
    while (elementAtCaret != null) {
      for (Class<? extends PsiElement> type : types) {
        if (type.isInstance(elementAtCaret)) {
          return Optional.of(elementAtCaret);
        }
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
            false,
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

  private static InfillRequestDetails createInfillRequest(int offsetInFile, Document document) {
    int begin = Integer.max(0, offsetInFile - MAX_OFFSET);
    int end = Integer.min(document.getTextLength(), offsetInFile + MAX_OFFSET);
    return createInfillRequest(document, offsetInFile, begin, end);
  }

  private static InfillRequestDetails createInfillRequest(
      Document document,
      int caretOffset,
      int start,
      int end) {
    return new InfillRequestDetails(
        document.getText(new TextRange(start, caretOffset)),
        document.getText(new TextRange(caretOffset, end)));
  }
}
