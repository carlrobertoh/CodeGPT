package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import java.util.function.Consumer;

class EventListener implements CompletionEventListener {

  private final Conversation conversation;
  private final Message message;
  private final Consumer<String> errorListener;
  private final Runnable completeListener;
  private final boolean isRetry;

  EventListener(
      Conversation conversation,
      Message message,
      Runnable completeListener,
      Consumer<String> errorListener,
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
  public void onFailure(String errorMessage) {
    saveConversation(errorMessage);
    errorListener.accept("\n" + errorMessage);
    completeListener.run();
  }

  private void saveConversation(String response) {
    var conversationsState = ConversationsState.getInstance();
    var conversationMessages = conversation.getMessages();

    if (isRetry && !conversationMessages.isEmpty()) {
      conversationMessages.remove(conversationMessages.size() - 1);
    }

    message.setResponse(response);
    conversationsState.saveConversation(conversation);
  }
}
