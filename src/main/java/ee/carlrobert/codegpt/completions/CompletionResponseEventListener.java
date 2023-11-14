package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.util.List;

public interface CompletionResponseEventListener {

  default void handleMessage(String message) {}

  default void handleError(ErrorDetails error, Throwable ex) {}

  default void handleTokensExceeded(Conversation conversation, Message message) {}

  default void handleCompleted(
      String fullMessage,
      Message message,
      Conversation conversation,
      boolean retry) {}

  default void handleSerpResults(List<YouSerpResult> results, Message message) {}
}
