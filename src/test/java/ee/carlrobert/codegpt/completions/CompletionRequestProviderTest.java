package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import testsupport.IntegrationTest;

public class CompletionRequestProviderTest extends IntegrationTest {

  public void testChatCompletionRequestWithSystemPromptOverride() {
    OpenAICredentialManager.getInstance().setCredential("TEST_API_KEY");
    ConfigurationSettings.getCurrentState().setSystemPrompt("TEST_SYSTEM_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var firstMessage = createDummyMessage(500);
    var secondMessage = createDummyMessage(250);
    conversation.addMessage(firstMessage);
    conversation.addMessage(secondMessage);

    var request = new CompletionRequestProvider(conversation)
        .buildOpenAIChatCompletionRequest(
            OpenAIChatCompletionModel.GPT_3_5.getCode(),
            new CallParameters(
                conversation,
                ConversationType.DEFAULT,
                new Message("TEST_CHAT_COMPLETION_PROMPT"),
                false));

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system", "TEST_SYSTEM_PROMPT"),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", firstMessage.getResponse()),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", secondMessage.getResponse()),
            tuple("user", "TEST_CHAT_COMPLETION_PROMPT"));
  }

  public void testChatCompletionRequestWithoutSystemPromptOverride() {
    var conversation = ConversationService.getInstance().startConversation();
    var firstMessage = createDummyMessage(500);
    var secondMessage = createDummyMessage(250);
    conversation.addMessage(firstMessage);
    conversation.addMessage(secondMessage);

    var request = new CompletionRequestProvider(conversation)
        .buildOpenAIChatCompletionRequest(
            OpenAIChatCompletionModel.GPT_3_5.getCode(),
            new CallParameters(
                conversation,
                ConversationType.DEFAULT,
                new Message("TEST_CHAT_COMPLETION_PROMPT"),
                false));

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system", COMPLETION_SYSTEM_PROMPT),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", firstMessage.getResponse()),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", secondMessage.getResponse()),
            tuple("user", "TEST_CHAT_COMPLETION_PROMPT"));
  }

  public void testChatCompletionRequestRetry() {
    ConfigurationSettings.getCurrentState().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    var conversation = ConversationService.getInstance().startConversation();
    var firstMessage = createDummyMessage("FIRST_TEST_PROMPT", 500);
    var secondMessage = createDummyMessage("SECOND_TEST_PROMPT", 250);
    conversation.addMessage(firstMessage);
    conversation.addMessage(secondMessage);

    var request = new CompletionRequestProvider(conversation)
        .buildOpenAIChatCompletionRequest(
            OpenAIChatCompletionModel.GPT_3_5.getCode(),
            new CallParameters(
                conversation,
                ConversationType.DEFAULT,
                secondMessage,
                true));

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system", COMPLETION_SYSTEM_PROMPT),
            tuple("user", "FIRST_TEST_PROMPT"),
            tuple("assistant", firstMessage.getResponse()),
            tuple("user", "SECOND_TEST_PROMPT"));
  }

  public void testReducedChatCompletionRequest() {
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(createDummyMessage(50));
    conversation.addMessage(createDummyMessage(100));
    conversation.addMessage(createDummyMessage(150));
    conversation.addMessage(createDummyMessage(1000));
    var remainingMessage = createDummyMessage(2000);
    conversation.addMessage(remainingMessage);
    conversation.discardTokenLimits();

    var request = new CompletionRequestProvider(conversation)
        .buildOpenAIChatCompletionRequest(
            OpenAIChatCompletionModel.GPT_3_5.getCode(),
            new CallParameters(
                conversation,
                ConversationType.DEFAULT,
                new Message("TEST_CHAT_COMPLETION_PROMPT"),
                false));

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system", COMPLETION_SYSTEM_PROMPT),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", remainingMessage.getResponse()),
            tuple("user", "TEST_CHAT_COMPLETION_PROMPT"));
  }

  public void testTotalUsageExceededException() {
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(createDummyMessage(1500));
    conversation.addMessage(createDummyMessage(1500));
    conversation.addMessage(createDummyMessage(1500));

    assertThrows(TotalUsageExceededException.class,
        () -> new CompletionRequestProvider(conversation)
            .buildOpenAIChatCompletionRequest(
                OpenAIChatCompletionModel.GPT_3_5.getCode(),
                new CallParameters(
                    conversation,
                    ConversationType.DEFAULT,
                    createDummyMessage(100),
                    false)));
  }

  private Message createDummyMessage(int tokenSize) {
    return createDummyMessage("TEST_PROMPT", tokenSize);
  }

  private Message createDummyMessage(String prompt, int tokenSize) {
    var message = new Message(prompt);
    // 'zz' = 1 token, prompt = 6 tokens, 7 tokens per message (GPT-3),
    message.setResponse("zz".repeat((tokenSize) - 6 - 7));
    return message;
  }
}
