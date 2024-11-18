package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedCodeActionInlay;
import ee.carlrobert.codegpt.util.file.FileUtil;

public class CodeActionProcessor implements ActionProcessor {

  @Override
  public void process(Message message, AppliedActionInlay action, StringBuilder promptBuilder) {
    if (!(action instanceof AppliedCodeActionInlay codeAction)) {
      throw new IllegalArgumentException("Invalid action type");
    }
    processCodeAction(codeAction, promptBuilder);
  }

  private void processCodeAction(AppliedCodeActionInlay action, StringBuilder promptBuilder) {
    promptBuilder
        .append("\n```%s\n".formatted(FileUtil.getFileExtension(action.getEditorFile().getName())))
        .append(action.getCode())
        .append("\n```\n");
  }
}
