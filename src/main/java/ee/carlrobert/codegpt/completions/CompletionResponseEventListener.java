package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.events.CodeGPTEvent;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;

public interface CompletionResponseEventListener {

  default void handleMessage(String message) {
  }

  default void handleError(ErrorDetails error, Throwable ex) {
  }

  default void handleTokensExceeded(Conversation conversation, Message message) {
  }

  default void handleCompleted(String fullMessage, CallParameters callParameters) {
  }

  default void handleCodeGPTEvent(CodeGPTEvent event) {
  }
}
