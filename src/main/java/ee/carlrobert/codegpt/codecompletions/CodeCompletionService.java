package ee.carlrobert.codegpt.codecompletions;

import static com.intellij.openapi.components.Service.Level.PROJECT;
import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
import com.intellij.util.concurrency.annotations.RequiresEdt;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import ee.carlrobert.codegpt.actions.CodeCompletionEnabledListener;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.KeyStroke;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

@Service(PROJECT)
@ParametersAreNonnullByDefault
public final class CodeCompletionService implements Disposable {

  public static final String APPLY_INLAY_ACTION_ID = "ApplyInlayAction";
  public static final int MAX_OFFSET = 4000;

  private static final Logger LOG = Logger.getInstance(CodeCompletionService.class);

  private final CallDebouncer callDebouncer;

  private CodeCompletionService(Project project) {
    this.callDebouncer = new CallDebouncer(project);
    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(
            CodeCompletionEnabledListener.TOPIC,
            (CodeCompletionEnabledListener) (completionsEnabled) -> {
              if (!completionsEnabled) {
                callDebouncer.cancelPreviousCall();
              }
            });
  }

  public static CodeCompletionService getInstance(Project project) {
    return project.getService(CodeCompletionService.class);
  }

  public boolean isCompletionAllowed(PsiElement elementAtCaret) {
    return elementAtCaret instanceof PsiWhiteSpace;
  }

  public void handleCompletions(Editor editor, int offset) {
    Project project = editor.getProject();
    if (project == null
        || project.isDisposed()
        || !ConfigurationState.getInstance().isCodeCompletionsEnabled()
        || !EditorUtil.isSelectedEditor(editor)
        || editor.isViewer()
        || editor.isOneLineMode()
    ) {
      return;
    }

    var document = editor.getDocument();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null) {
      return;
    }

    PsiElement elementAtCaret = ReadAction.compute(() -> psiFile.findElementAt(offset));
    var completionService = CodeCompletionService.getInstance(project);
    if (!completionService.isCompletionAllowed(elementAtCaret)) {
      return;
    }

    callDebouncer.debounce(
        Void.class,
        (progressIndicator) -> completionService.fetchCodeCompletion(
            elementAtCaret,
            offset,
            document,
            new CodeCompletionEventListener(editor, offset, progressIndicator)),
        500,
        TimeUnit.MILLISECONDS);
  }

  /**
   * Fetches code-completion (FIM) for the given position ({@code offsetInFile}) in the file. <br/>
   * By default tries to find an enclosing {@link PsiMethod} or {@link PsiClass} for the given
   * {@code offsetInFile} and only uses their content instead of the entire file's content. If no
   * such enclosing {@link PsiElement} can be found, the file's entire content is used instead.
   *
   * @param elementAtCaret PsiElement at caret
   * @param offsetInFile   Global offset in the file.
   * @param document       If the offset is not enclosed in a {@link PsiMethod} nor a
   *                       {@link PsiClass}, the entire file content is used for completion.
   * @return Completion String
   */
  @RequiresBackgroundThread
  public EventSource fetchCodeCompletion(
      PsiElement elementAtCaret,
      int offsetInFile,
      Document document,
      CompletionEventListener eventListener) {
    InfillRequestDetails requestDetails = tryFindEnclosingPsiElementTextRange(
        List.of(PsiMethod.class, PsiClass.class), elementAtCaret)
        .map(textRange -> createInfillRequest(
            document,
            offsetInFile,
            textRange.getStartOffset(),
            textRange.getEndOffset())
        )
        .orElse(createInfillRequest(document, offsetInFile));
    return CompletionRequestService.getInstance()
        .getCodeCompletionAsync(requestDetails, eventListener);
  }

  @RequiresEdt
  public void addInlays(Editor editor, int caretOffset, String inlayText) {
    List<String> linesList = inlayText.lines().collect(toList());
    String firstLine = linesList.get(0);
    String restOfLines = linesList.size() > 1
        ? String.join("\n", linesList.subList(1, linesList.size()))
        : null;
    InlayModel inlayModel = editor.getInlayModel();

    if (!firstLine.isEmpty()) {
      editor.putUserData(SINGLE_LINE_INLAY, inlayModel.addInlineElement(
          caretOffset,
          true,
          Integer.MAX_VALUE,
          new InlayInlineElementRenderer(firstLine)));
    }

    if (restOfLines != null && !restOfLines.isEmpty()) {
      editor.putUserData(MULTI_LINE_INLAY, inlayModel.addBlockElement(
          caretOffset,
          true,
          false,
          Integer.MAX_VALUE,
          new InlayBlockElementRenderer(restOfLines)));
    }

    registerApplyCompletionAction(() -> WriteCommandAction.runWriteCommandAction(
        editor.getProject(),
        () -> applyCompletion(editor, inlayText)));
  }

  @RequiresWriteLock
  private void applyCompletion(Editor editor, String text) {
    if (editor.isDisposed()) {
      LOG.warn("Editor is already disposed");
      return;
    }

    var inlayKeys = List.of(SINGLE_LINE_INLAY, MULTI_LINE_INLAY);
    for (var key : inlayKeys) {
      Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(key);
      if (inlay != null) {
        applyCompletion(editor, text, inlay.getOffset());
        CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
        return;
      }
    }
  }

  @RequiresWriteLock
  private void applyCompletion(Editor editor, String text, int offset) {
    Document document = editor.getDocument();
    document.insertString(offset, text);
    editor.getCaretModel().moveToOffset(offset + text.length());
    EditorUtil.reformatDocument(
        requireNonNull(editor.getProject()),
        document,
        offset,
        offset + text.length());
  }

  @RequiresReadLock
  private Optional<TextRange> tryFindEnclosingPsiElementTextRange(
      List<Class<? extends PsiElement>> types,
      PsiElement elementAtCaret) {
    return ReadAction.compute(() -> {
      var element = elementAtCaret;
      while (element != null) {
        for (Class<? extends PsiElement> type : types) {
          if (type.isInstance(element)) {
            return Optional.of(element.getTextRange());
          }
        }
        element = element.getParent();
      }

      return Optional.empty();
    });
  }

  @Override
  public void dispose() {
    callDebouncer.shutdown();
  }

  private void registerApplyCompletionAction(Runnable onApply) {
    var actionManager = ActionManager.getInstance();
    actionManager.registerAction(
        APPLY_INLAY_ACTION_ID,
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            onApply.run();
          }
        });
    KeymapManager.getInstance().getActiveKeymap().addShortcut(
        APPLY_INLAY_ACTION_ID,
        new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
  }

  private static InfillRequestDetails createInfillRequest(Document document, int offsetInFile) {
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
