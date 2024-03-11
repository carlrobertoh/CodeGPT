package ee.carlrobert.codegpt.codecompletions;

import static com.intellij.openapi.components.Service.Level.PROJECT;
import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.lookup.LookupManager;
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
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.concurrency.annotations.RequiresEdt;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.actions.CodeCompletionEnabledListener;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.util.EditorUtil;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;

@Service(PROJECT)
@ParametersAreNonnullByDefault
public final class CodeCompletionService implements Disposable {

  public static final String APPLY_INLAY_ACTION_ID = "ApplyInlayAction";

  private static final Logger LOG = Logger.getInstance(CodeCompletionService.class);

  private final Project project;
  private final CallDebouncer callDebouncer;

  private CodeCompletionService(Project project) {
    this.project = project;
    this.callDebouncer = new CallDebouncer(project);

    subscribeToFeatureToggleEvents();
  }

  public static CodeCompletionService getInstance(Project project) {
    return project.getService(CodeCompletionService.class);
  }

  public void cancelPreviousCall() {
    callDebouncer.cancelPreviousCall();
  }

  public void handleCompletions(Editor editor, int offset) {
    PREVIOUS_INLAY_TEXT.set(editor, null);

    if (project.isDisposed()
        || TypeOverHandler.getPendingTypeOverAndReset(editor)
        || !ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled()
        || !EditorUtil.isSelectedEditor(editor)
        || LookupManager.getActiveLookup(editor) != null
        || editor.isViewer()
        || editor.isOneLineMode()) {
      return;
    }

    var document = editor.getDocument();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null) {
      return;
    }

    var request = InfillRequestDetails.fromDocumentWithMaxOffset(document, offset);
    if (Stream.of(request.getSuffix(), request.getPrefix())
        .anyMatch(item -> item == null || item.isEmpty())) {
      return;
    }

    callDebouncer.debounce(
        Void.class,
        (progressIndicator) -> CompletionRequestService.getInstance().getCodeCompletionAsync(
            request,
            new CodeCompletionEventListener(editor, offset, request, progressIndicator)),
        750,
        TimeUnit.MILLISECONDS);
  }

  @RequiresEdt
  public void addInlays(Editor editor, int caretOffset, String inlayText) {
    PREVIOUS_INLAY_TEXT.set(editor, inlayText);

    if (LookupManager.getActiveLookup(editor) != null || inlayText.isBlank()) {
      return;
    }

    List<String> linesList = inlayText.lines().collect(toList());
    var firstLine = linesList.get(0);
    var restOfLines = linesList.size() > 1
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
        project,
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
        break;
      }
    }
    editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, null);
  }

  @RequiresWriteLock
  private void applyCompletion(Editor editor, String text, int offset) {
    Document document = editor.getDocument();
    try {
      document.insertString(offset, text);
    } catch (PatternSyntaxException e) {
      // ignore
    }
    editor.getCaretModel().moveToOffset(offset + text.length());
    if (ConfigurationSettings.getCurrentState().isAutoFormattingEnabled()) {
      EditorUtil.reformatDocument(project, document, offset, offset + text.length());
    }
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

  private void subscribeToFeatureToggleEvents() {
    ApplicationManager.getApplication()
        .getMessageBus()
        .connect(this)
        .subscribe(
            CodeCompletionEnabledListener.TOPIC,
            (CodeCompletionEnabledListener) (completionsEnabled) -> {
              if (!completionsEnabled) {
                cancelPreviousCall();
              }
            });
  }
}
