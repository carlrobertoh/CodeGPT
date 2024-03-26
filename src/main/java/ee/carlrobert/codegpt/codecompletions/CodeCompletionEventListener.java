package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static java.util.Objects.requireNonNull;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.OpenSettingsAction;
import ee.carlrobert.codegpt.treesitter.CodeCompletionParserFactory;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import javax.swing.SwingUtilities;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.Nullable;

class CodeCompletionEventListener implements CompletionEventListener<String> {

  private static final Logger LOG = Logger.getInstance(CodeCompletionEventListener.class);
  private final Editor editor;
  private final int caretOffset;
  private final BackgroundableProcessIndicator progressIndicator;
  private final CodeCompletionService completionService;

  public CodeCompletionEventListener(
      Editor editor,
      int caretOffset,
      CodeCompletionService completionService,
      @Nullable BackgroundableProcessIndicator progressIndicator) {
    this.editor = editor;
    this.caretOffset = caretOffset;
    this.completionService = completionService;
    this.progressIndicator = progressIndicator;
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    if (progressIndicator != null) {
      progressIndicator.processFinish();
    }
    PREVIOUS_INLAY_TEXT.set(editor, messageBuilder.toString());
  }

  @Override
  public void onMessage(String message, EventSource eventSource) {
    CompletionEventListener.super.onMessage(message, eventSource);
    completionService.registerTemporaryActions(editor);
    SwingUtilities.invokeLater(() -> {
      if (editor.getCaretModel().getOffset() == caretOffset) {
        var service = CodeCompletionService.getInstance(requireNonNull(editor.getProject()));
        service.updateInlays(editor, caretOffset, message);
      }
    });
  }

  @Override
  public void onError(ErrorDetails error, Throwable ex) {
    completionService.unRegisterTemporaryActions();
    // TODO: temp fix
    if (ex instanceof IOException && "Canceled".equals(error.getMessage())) {
      return;
    }

    LOG.error(error.getMessage(), ex);
    if (progressIndicator != null) {
      progressIndicator.processFinish();
    }
    Notifications.Bus.notify(OverlayUtil.getDefaultNotification(
            String.format(
                CodeGPTBundle.get("notification.completionError.description"),
                error.getMessage()),
            NotificationType.ERROR)
        .addAction(new OpenSettingsAction()), editor.getProject());
    CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
  }

  @Override
  public void onCancelled(StringBuilder messageBuilder) {
    LOG.debug("Completion cancelled");
    completionService.unRegisterTemporaryActions();
    if (progressIndicator != null) {
      progressIndicator.processFinish();
    }
    CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
  }
}
