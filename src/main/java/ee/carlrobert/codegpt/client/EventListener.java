package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import java.util.function.Consumer;

class EventListener implements CompletionEventListener {

  private final Conversation conversation;
  private final Message message;
  private final Consumer<ErrorDetails> errorListener;
  private final Runnable completeListener;
  private final boolean isRetry;

  EventListener(
      Conversation conversation,
      Message message,
      Runnable completeListener,
      Consumer<ErrorDetails> errorListener,
      boolean isRetry) {
    this.conversation = conversation;
    this.completeListener = completeListener;
    this.errorListener = errorListener;
    this.message = message;
    this.isRetry = isRetry;
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    saveConversation(messageBuilder.toString());
    completeListener.run();
  }

  @Override
  public void onError(ErrorDetails errorDetails) {
    errorListener.accept(errorDetails);
  }

  private void saveConversation(String response) {
    var conversationMessages = conversation.getMessages();
    if (isRetry && !conversationMessages.isEmpty()) {
      conversationMessages.remove(conversationMessages.size() - 1);
    }

    message.setResponse(response);
    conversation.addMessage(message);
    ConversationsState.getInstance().saveConversation(conversation);
  }
}
