package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedCodeActionInlay;
import ee.carlrobert.codegpt.util.file.FileUtil;

public class CodeActionProcessor implements ActionProcessor {

  @Override
  public void process(Message message, AppliedActionInlay action, Editor editor,
      StringBuilder promptBuilder) {
    if (!(action instanceof AppliedCodeActionInlay codeAction)) {
      throw new IllegalArgumentException("Invalid action type");
    }
    processCodeAction(codeAction, editor, promptBuilder);
  }

  private void processCodeAction(AppliedCodeActionInlay action, Editor editor,
      StringBuilder promptBuilder) {
    promptBuilder
        .append("\n```%s\n".formatted(FileUtil.getFileExtension(editor.getVirtualFile().getName())))
        .append(action.getCode())
        .append("\n```\n");
  }
}
