package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static java.util.Objects.requireNonNull;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.OpenSettingsAction;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;

class CodeCompletionEventListener implements CompletionEventListener<String> {

  private static final Logger LOG = Logger.getInstance(CodeCompletionEventListener.class);

  private final Editor editor;
  private final int caretOffset;
  private final InfillRequest requestDetails;
  private final BackgroundableProcessIndicator progressIndicator;

  public CodeCompletionEventListener(
      Editor editor,
      int caretOffset,
      InfillRequest requestDetails,
      @Nullable BackgroundableProcessIndicator progressIndicator) {
    this.editor = editor;
    this.caretOffset = caretOffset;
    this.requestDetails = requestDetails;
    this.progressIndicator = progressIndicator;
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    if (progressIndicator != null) {
      progressIndicator.processFinish();
    }

    try {
      var project = editor.getProject();
      if (project != null) {
        var response = messageBuilder.toString();
        handleComplete(
            response.contains("\n") ? response.substring(0, response.indexOf("\n")) : response);
      }
    } catch (IllegalArgumentException e) {
      handleComplete(messageBuilder.toString());
    }
  }

  private void handleComplete(String processedOutput) {
    PREVIOUS_INLAY_TEXT.set(editor, processedOutput);
    CodeGPTEditorManager.getInstance().disposeEditorInlays(editor);
    SwingUtilities.invokeLater(() -> {
      if (editor.getCaretModel().getOffset() == caretOffset && !processedOutput.isEmpty()) {
        CodeCompletionServiceOld
            .getInstance(requireNonNull(editor.getProject()))
            .addInlays(editor, caretOffset, processedOutput);
      }
    });
  }

  @Override
  public void onError(ErrorDetails error, Throwable ex) {
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
  }

  @Override
  public void onCancelled(StringBuilder messageBuilder) {
    LOG.debug("Completion cancelled");
    if (progressIndicator != null) {
      progressIndicator.processFinish();
    }
  }
}
