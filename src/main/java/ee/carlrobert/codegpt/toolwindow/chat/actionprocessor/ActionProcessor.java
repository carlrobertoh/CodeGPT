package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;

public interface ActionProcessor {

  void process(Message message, AppliedActionInlay action, StringBuilder promptBuilder);
}
