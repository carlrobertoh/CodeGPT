package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;

public interface ActionProcessor {

  void process(Message message, AppliedActionInlay action, Editor editor,
      StringBuilder promptBuilder);
}
