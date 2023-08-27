package ee.carlrobert.codegpt.conversations;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;

public class ConversationsStateTest extends BasePlatformTestCase {

  public void testStartNewDefaultConversation() {
    var modelSettings = ModelSettingsState.getInstance();
    modelSettings.setUseChatCompletion(true);
    modelSettings.setUseTextCompletion(false);
    modelSettings.setChatCompletionModel(ChatCompletionModel.GPT_3_5.getCode());

    var conversation = ConversationService.getInstance().startConversation();

    assertThat(conversation).isEqualTo(ConversationsState.getCurrentConversation());
    assertThat(conversation)
        .extracting("clientCode", "model")
        .containsExactly(ClientCode.CHAT_COMPLETION, "gpt-3.5-turbo");
  }

  public void testSaveConversation() {
    var service = ConversationService.getInstance();
    var conversation = service.createConversation(ClientCode.CHAT_COMPLETION);
    service.addConversation(conversation);
    var message = new Message("TEST_PROMPT");
    message.setResponse("TEST_RESPONSE");
    conversation.addMessage(message);

    service.saveConversation(conversation);

    var currentConversation = ConversationsState.getCurrentConversation();
    assertThat(currentConversation).isNotNull();
    assertThat(currentConversation.getMessages())
        .flatExtracting("prompt", "response")
        .containsExactly("TEST_PROMPT", "TEST_RESPONSE");
  }

  public void testGetPreviousConversation() {
    var service = ConversationService.getInstance();
    var firstConversation = service.startConversation();
    service.startConversation();

    var previousConversation = service.getPreviousConversation();

    assertThat(previousConversation.isPresent()).isTrue();
    assertThat(previousConversation.get()).isEqualTo(firstConversation);
  }

  public void testGetNextConversation() {
    var service = ConversationService.getInstance();
    var firstConversation = service.startConversation();
    var secondConversation = service.startConversation();
    ConversationsState.getInstance().setCurrentConversation(firstConversation);

    var nextConversation = service.getNextConversation();

    assertThat(nextConversation.isPresent()).isTrue();
    assertThat(nextConversation.get()).isEqualTo(secondConversation);
  }

  public void testDeleteSelectedConversation() {
    var service = ConversationService.getInstance();
    var firstConversation = service.startConversation();
    service.startConversation();

    service.deleteSelectedConversation();

    assertThat(ConversationsState.getCurrentConversation()).isEqualTo(firstConversation);
    assertThat(service.getSortedConversations().size()).isEqualTo(1);
    assertThat(service.getSortedConversations())
        .extracting("id")
        .containsExactly(firstConversation.getId());
  }

  public void testClearAllConversations() {
    var service = ConversationService.getInstance();
    service.startConversation();
    service.startConversation();

    service.clearAll();

    assertThat(ConversationsState.getCurrentConversation()).isNull();
    assertThat(service.getSortedConversations().size()).isEqualTo(0);
  }
}
