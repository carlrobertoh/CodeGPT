package ee.carlrobert.codegpt.state.conversations;


import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;

public class ConversationsStateTest extends BasePlatformTestCase {

  public void testStartNewDefaultConversation() {
    var settings = SettingsState.getInstance();
    settings.isChatCompletionOptionSelected = true;
    settings.isTextCompletionOptionSelected = false;
    settings.chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();

    var conversation = ConversationsState.getInstance().startConversation();

    assertThat(conversation).isEqualTo(ConversationsState.getCurrentConversation());
    assertThat(conversation)
        .extracting("clientCode", "model")
        .containsExactly(ClientCode.CHAT_COMPLETION, "gpt-3.5-turbo");
  }

  public void testSaveConversation() {
    var instance = ConversationsState.getInstance();
    var conversation = instance.createConversation(ClientCode.CHAT_COMPLETION);
    instance.addConversation(conversation);
    var message = new Message("TEST_PROMPT");
    message.setResponse("TEST_RESPONSE");
    conversation.addMessage(message);

    instance.saveConversation(conversation);

    var currentConversation = ConversationsState.getCurrentConversation();
    assertThat(currentConversation).isNotNull();
    assertThat(currentConversation.getMessages())
        .flatExtracting("prompt", "response")
        .containsExactly("TEST_PROMPT", "TEST_RESPONSE");
  }

  public void testGetPreviousConversation() {
    var instance = ConversationsState.getInstance();
    var firstConversation = instance.startConversation();
    instance.startConversation();

    var previousConversation = instance.getPreviousConversation();

    assertThat(previousConversation.isPresent()).isTrue();
    assertThat(previousConversation.get()).isEqualTo(firstConversation);
  }

  public void testGetNextConversation() {
    var instance = ConversationsState.getInstance();
    var firstConversation = instance.startConversation();
    var secondConversation = instance.startConversation();
    instance.setCurrentConversation(firstConversation);

    var nextConversation = instance.getNextConversation();

    assertThat(nextConversation.isPresent()).isTrue();
    assertThat(nextConversation.get()).isEqualTo(secondConversation);
  }

  public void testDeleteSelectedConversation() {
    var instance = ConversationsState.getInstance();
    var firstConversation = instance.startConversation();
    instance.startConversation();

    instance.deleteSelectedConversation();

    assertThat(ConversationsState.getCurrentConversation()).isEqualTo(firstConversation);
    assertThat(instance.getSortedConversations().size()).isEqualTo(1);
    assertThat(instance.getSortedConversations())
        .extracting("id")
        .containsExactly(firstConversation.getId());
  }

  public void testClearAllConversations() {
    var instance = ConversationsState.getInstance();
    instance.startConversation();
    instance.startConversation();

    instance.clearAll();

    assertThat(ConversationsState.getCurrentConversation()).isNull();
    assertThat(instance.getSortedConversations().size()).isEqualTo(0);
  }
}
