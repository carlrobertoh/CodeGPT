package ee.carlrobert.codegpt.codecompletions;

import static com.intellij.openapi.components.Service.Level.PROJECT;
import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static java.lang.Integer.min;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;

@Service(PROJECT)
public final class CodeCompletionService implements Disposable {

  // accept all completion text by `TAB`.
  public static final String APPLY_INLAY_ACTION_ACCEPT_ALL_ID = "ApplyInlayAction_ACCEPT_ALL";
  // accept n line completion text by `CTRL + n`
  public static final String APPLY_INLAY_ACTION_ACCEPT_1_ID = "ApplyInlayAction_ACCEPT_1";
  public static final String APPLY_INLAY_ACTION_ACCEPT_2_ID = "ApplyInlayAction_ACCEPT_2";
  public static final String APPLY_INLAY_ACTION_ACCEPT_3_ID = "ApplyInlayAction_ACCEPT_3";
  public static final String APPLY_INLAY_ACTION_ACCEPT_4_ID = "ApplyInlayAction_ACCEPT_4";
  public static final String APPLY_INLAY_ACTION_ACCEPT_5_ID = "ApplyInlayAction_ACCEPT_5";
  // cancel completion by `ESC`
  public static final String APPLY_INLAY_ACTION_CANCEL_ID = "ApplyInlayAction_CANCEL";

  public static final String[] subActionIdList = {
    APPLY_INLAY_ACTION_ACCEPT_1_ID,
    APPLY_INLAY_ACTION_ACCEPT_2_ID,
    APPLY_INLAY_ACTION_ACCEPT_3_ID,
    APPLY_INLAY_ACTION_ACCEPT_4_ID,
    APPLY_INLAY_ACTION_ACCEPT_5_ID
  };

  private static final Logger LOG = Logger.getInstance(CodeCompletionService.class);

  private final Project project;
  private final CallDebouncer callDebouncer;

  private static CodeCompletionTriggerType triggerType = CodeCompletionTriggerType.AUTOMATIC;

  private CodeCompletionService(Project project) {
    this.project = project;
    this.callDebouncer = new CallDebouncer(project);

    subscribeToFeatureToggleEvents();
  }

  public static CodeCompletionService getInstance(Project project) {
    return project.getService(CodeCompletionService.class);
  }

  public void cancelPreviousCall() {
    unRegisterTemporaryActions();
    callDebouncer.cancelPreviousCall();
  }

  public void triggerCompletions(Editor editor, int offset, CodeCompletionTriggerType triggerType) {
    PREVIOUS_INLAY_TEXT.set(editor, null);
    CodeCompletionService.triggerType = triggerType;

    if (project.isDisposed()
        || TypeOverHandler.getPendingTypeOverAndReset(editor)
        || !EditorUtil.isSelectedEditor(editor)
        || LookupManager.getActiveLookup(editor) != null
        || editor.isViewer()
        || editor.isOneLineMode()) {
      return;
    }

    if (triggerType == CodeCompletionTriggerType.AUTOMATIC &&
        !ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled()) {
      return;
    }

    var document = editor.getDocument();
    if (triggerType == CodeCompletionTriggerType.AUTOMATIC) {
      PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
      if (psiFile == null) {
        return;
      }
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
            new CodeCompletionEventListener(editor, offset, this, progressIndicator)),
        750,
        TimeUnit.MILLISECONDS);
  }

  @RequiresEdt
  public void updateInlays(Editor editor, int caretOffset, String token) {
    String currentInlayText = PREVIOUS_INLAY_TEXT.get(editor);
    if (currentInlayText == null) {
      currentInlayText = "";
    }
    String updatedInlayText = currentInlayText + token;
    PREVIOUS_INLAY_TEXT.set(editor, updatedInlayText);

    List<String> linesList = updatedInlayText.lines().collect(toList());
    if (!linesList.isEmpty()) {
      var firstLine = linesList.get(0);
      var restOfLines = linesList.size() > 1
          ? String.join("\n", linesList.subList(1, linesList.size()))
          : null;
      InlayModel inlayModel = editor.getInlayModel();

      updateInlineInlay(editor, caretOffset, inlayModel, firstLine);
      updateBlockInlay(editor, caretOffset, inlayModel, restOfLines);
    }
  }

  private void updateInlineInlay(Editor editor, int caretOffset, InlayModel inlayModel, String firstLine) {
    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(SINGLE_LINE_INLAY);
    if (inlay != null) {
      inlay.dispose();
    }
    if (!firstLine.isEmpty()) {
      editor.putUserData(SINGLE_LINE_INLAY, inlayModel.addInlineElement(
          caretOffset,
          true,
          Integer.MAX_VALUE,
          new InlayInlineElementRenderer(firstLine)));
    }
  }

  private void updateBlockInlay(Editor editor, int caretOffset, InlayModel inlayModel, String restOfLines) {
    Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(MULTI_LINE_INLAY);
    if (inlay != null) {
      inlay.dispose();
    }
    if (restOfLines != null && !restOfLines.isEmpty()) {
      editor.putUserData(MULTI_LINE_INLAY, inlayModel.addBlockElement(
          caretOffset,
          true,
          false,
          Integer.MAX_VALUE,
          new InlayBlockElementRenderer(restOfLines)));
    }
  }

  @RequiresWriteLock
  private void acceptCompletion(Editor editor, String actionId) {
    if (editor.isDisposed()) {
      LOG.warn("Editor is already disposed");
      return;
    }
    String text = PREVIOUS_INLAY_TEXT.get(editor);
    if (text != null && !text.isEmpty()) {
      var inlayKeys = List.of(SINGLE_LINE_INLAY, MULTI_LINE_INLAY);
      for (var key : inlayKeys) {
        Inlay<EditorCustomElementRenderer> inlay = editor.getUserData(key);
        if (inlay != null) {
          acceptCompletion(editor, text, actionId, inlay.getOffset());
          CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
          return;
        }
      }
    }
    editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, null);
  }

  @RequiresWriteLock
  private void acceptCompletion(Editor editor, String text, String actionId, int offset) {
    Document document = editor.getDocument();
    try {
      if (actionId.equals(APPLY_INLAY_ACTION_ACCEPT_ALL_ID)) {
        document.insertString(offset, text);
      } else if (actionId.equals(APPLY_INLAY_ACTION_CANCEL_ID)) {
        text = "";
        CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
        cancelPreviousCall();
      } else {
        int n = 1;
        switch (actionId) {
          case APPLY_INLAY_ACTION_ACCEPT_1_ID:
            n = 1;
            break;
          case APPLY_INLAY_ACTION_ACCEPT_2_ID:
            n = 2;
            break;
          case APPLY_INLAY_ACTION_ACCEPT_3_ID:
            n = 3;
            break;
          case APPLY_INLAY_ACTION_ACCEPT_4_ID:
            n = 4;
            break;
          case APPLY_INLAY_ACTION_ACCEPT_5_ID:
            n = 5;
            break;
        }
        String[] lines = text.split("\n");
        n = min(n, lines.length);
        text = String.join("\n", Arrays.copyOfRange(lines, 0, n));
        document.insertString(offset, text);
      }
    } catch (PatternSyntaxException e) {
      // ignore
    }
    editor.getCaretModel().moveToOffset(offset + text.length());
    if (ConfigurationSettings.getCurrentState().isAutoFormattingEnabled()) {
      EditorUtil.reformatDocument(project, document, offset, offset + text.length());
    }
    this.unRegisterTemporaryActions();
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
        APPLY_INLAY_ACTION_ACCEPT_ALL_ID,
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            onApply.run();
          }
        });
    KeymapManager.getInstance().getActiveKeymap().addShortcut(
            APPLY_INLAY_ACTION_ACCEPT_ALL_ID,
        new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
  }

  private void registerCancelCompletionAction(Runnable onApply) {
    var actionManager = ActionManager.getInstance();
    actionManager.registerAction(
        APPLY_INLAY_ACTION_CANCEL_ID,
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            onApply.run();
          }
        });
    KeymapManager.getInstance().getActiveKeymap().addShortcut(
        APPLY_INLAY_ACTION_CANCEL_ID,
        new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), null));
  }

  private void registerSubApplyCompletionActions(String subActionId, Runnable onApply) {
    int index = -1;
    for (int i = 0; i < subActionIdList.length; i++) {
      if (subActionIdList[i].equals(subActionId)) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      return;
    }
    var actionManager = ActionManager.getInstance();
    actionManager.registerAction(
        subActionId,
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            onApply.run();
          }
        });
    String shortcut = "ctrl " + (index + 1);
    LOG.debug("Register shortcut " + shortcut);
    KeymapManager.getInstance().getActiveKeymap().addShortcut(
        subActionId,
        new KeyboardShortcut(KeyStroke.getKeyStroke(shortcut), null));
  }

   public void registerTemporaryActions(Editor editor) {
     var actionManager = ActionManager.getInstance();
     if (actionManager.getAction(APPLY_INLAY_ACTION_ACCEPT_ALL_ID) != null) {
       return;
     }
     // register temporary actions

     // accept all
     registerApplyCompletionAction(() -> WriteCommandAction.runWriteCommandAction(
             project,
             () -> acceptCompletion(editor, APPLY_INLAY_ACTION_ACCEPT_ALL_ID)));
     // accept by lines
     for (String subAction : subActionIdList) {
       registerSubApplyCompletionActions(
               subAction,
               () -> WriteCommandAction.runWriteCommandAction(
                       project,
                       () -> acceptCompletion(editor, subAction)));
     }
     // cancel completion
     registerCancelCompletionAction(() -> WriteCommandAction.runWriteCommandAction(
             project,
             () -> acceptCompletion(editor, APPLY_INLAY_ACTION_CANCEL_ID)));
   }

   public void unRegisterTemporaryActions() {
     var manager = ActionManager.getInstance();
     if (manager.getAction(APPLY_INLAY_ACTION_ACCEPT_ALL_ID) == null) {
       return;
     }
     manager.unregisterAction(CodeCompletionService.APPLY_INLAY_ACTION_ACCEPT_ALL_ID);
     for (String subActionId : CodeCompletionService.subActionIdList) {
       manager.unregisterAction(subActionId);
     }
     manager.unregisterAction(CodeCompletionService.APPLY_INLAY_ACTION_CANCEL_ID);
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

