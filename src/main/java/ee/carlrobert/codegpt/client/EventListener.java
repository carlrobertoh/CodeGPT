package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import java.util.function.Consumer;

public class EventListener implements CompletionEventListener {

  private final Conversation conversation;
  private final Message message;
  private final Consumer<String> onAppend;
  private final Runnable onStopGenerating;
  private final boolean isRetry;

  public EventListener(Conversation conversation, Message message, Consumer<String> onAppend, Runnable onStopGenerating, boolean isRetry) {
    this.conversation = conversation;
    this.onStopGenerating = onStopGenerating;
    this.onAppend = onAppend;
    this.message = message;
    this.isRetry = isRetry;
  }

  @Override
  public void onComplete(StringBuilder messageBuilder) {
    saveConversation(messageBuilder.toString());
    onStopGenerating.run();
  }

  @Override
  public void onFailure(String errorMessage) {
    saveConversation(errorMessage);
    onAppend.accept("\n" + errorMessage);
    onStopGenerating.run();
  }

  private void saveConversation(String response) {
    var conversationsState = ConversationsState.getInstance();
    var conversationMessages = conversation.getMessages();

    if (isRetry && !conversationMessages.isEmpty()) {
      conversationMessages.remove(conversationMessages.size() - 1);
    }

    message.setResponse(response);
    conversation.addMessage(message);
    conversationsState.saveConversation(conversation);
  }
}
