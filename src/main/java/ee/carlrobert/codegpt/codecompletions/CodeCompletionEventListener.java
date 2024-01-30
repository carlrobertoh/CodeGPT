package ee.carlrobert.codegpt.codecompletions;

import static java.util.Objects.requireNonNull;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.OpenSettingsAction;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;

class CodeCompletionEventListener implements CompletionEventListener {

  private static final Logger LOG = Logger.getInstance(CodeCompletionEventListener.class);

  private final Editor editor;
  private final int caretOffset;

  public CodeCompletionEventListener(Editor editor, int caretOffset) {
    this.editor = editor;
    this.caretOffset = caretOffset;
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    var editorManager = CodeGPTEditorManager.getInstance();
    editorManager.disposeEditorInlays(editor);

    var inlayText = messageBuilder.toString();
    if (!inlayText.isEmpty()) {
      ApplicationManager.getApplication().invokeLater(() ->
          CodeCompletionService.getInstance(requireNonNull(editor.getProject()))
              .addInlays(editor, caretOffset, inlayText));
    }
  }

  @Override
  public void onError(ErrorDetails error, Throwable ex) {
    LOG.error(error.getMessage(), ex);
    Notifications.Bus.notify(OverlayUtil.getDefaultNotification(
            String.format(
                CodeGPTBundle.get("notification.completionError.description"),
                ex.getMessage()),
            NotificationType.ERROR)
        .addAction(new OpenSettingsAction()), editor.getProject());
  }
}
