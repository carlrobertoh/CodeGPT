package ee.carlrobert.codegpt.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.http.LocalCallbackServer;

public class CompletionRequestProviderTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testTextCompletionRequest() {
    var conversation = ConversationsState.getInstance()
        .createConversation(ClientCode.TEXT_COMPLETION);
    conversation.addMessage(new Message("TEST_PROMPT", "TEST_RESPONSE"));
    conversation.addMessage(new Message("TEST_PROMPT_2", "TEST_RESPONSE_2"));

    var request = new CompletionRequestProvider("TEST_TEXT_COMPLETION_PROMPT", conversation)
        .buildTextCompletionRequest(TextCompletionModel.DAVINCI.getCode());

    assertThat(request.getPrompt())
        .isEqualTo("You are ChatGPT, a large language model trained by OpenAI.\n"
            + "Answer in a markdown language, code blocks should contain language whenever possible.\n"
            + "Human: TEST_PROMPT\n"
            + "AI: TEST_RESPONSE\n"
            + "Human: TEST_PROMPT_2\n"
            + "AI: TEST_RESPONSE_2\n"
            + "Human: TEST_TEXT_COMPLETION_PROMPT\n"
            + "AI: \n");
  }

  public void testChatCompletionRequest() {
    var conversation = ConversationsState.getInstance().startConversation();
    var firstMessage = createMessage(500);
    var secondMessage = createMessage(250);
    conversation.addMessage(firstMessage);
    conversation.addMessage(secondMessage);

    var request = new CompletionRequestProvider("TEST_CHAT_COMPLETION_PROMPT", conversation)
        .buildChatCompletionRequest(ChatCompletionModel.GPT_3_5.getCode());

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system",
                "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", firstMessage.getResponse()),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", secondMessage.getResponse()),
            tuple("user", "TEST_CHAT_COMPLETION_PROMPT"));
  }

  public void testReducedChatCompletionRequest() {
    var conversation = ConversationsState.getInstance().startConversation();
    conversation.addMessage(createMessage(50));
    conversation.addMessage(createMessage(100));
    conversation.addMessage(createMessage(150));
    var firstRemainingMessage = createMessage(1000);
    var secondRemainingMessage = createMessage(2000);
    conversation.addMessage(firstRemainingMessage);
    conversation.addMessage(secondRemainingMessage);
    conversation.discardTokenLimits();

    var request = new CompletionRequestProvider("TEST_CHAT_COMPLETION_PROMPT", conversation)
        .buildChatCompletionRequest(ChatCompletionModel.GPT_3_5.getCode());

    assertThat(request.getMessages())
        .extracting("role", "content")
        .containsExactly(
            tuple("system",
                "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", firstRemainingMessage.getResponse()),
            tuple("user", "TEST_PROMPT"),
            tuple("assistant", secondRemainingMessage.getResponse()),
            tuple("user", "TEST_CHAT_COMPLETION_PROMPT"));
  }

  public void testTotalUsageExceededException() {
    var conversation = ConversationsState.getInstance().startConversation();
    conversation.addMessage(createMessage(1500));
    conversation.addMessage(createMessage(1500));
    conversation.addMessage(createMessage(1500));

    assertThrows(TotalUsageExceededException.class,
        () -> new CompletionRequestProvider("TEST_MESSAGE", conversation)
            .buildChatCompletionRequest(ChatCompletionModel.GPT_3_5.getCode()));
  }

  private Message createMessage(int tokenSize) {
    var message = new Message("TEST_PROMPT");
    // 'zz' = 1 token, prompt = 6 tokens, 7 tokens per message (GPT-3),
    message.setResponse("zz".repeat((tokenSize) - 6 - 7));
    return message;
  }
}
