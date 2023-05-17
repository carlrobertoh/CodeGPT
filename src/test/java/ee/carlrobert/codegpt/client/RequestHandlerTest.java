package ee.carlrobert.codegpt.client;

import static ee.carlrobert.openai.util.JSONUtil.jsonArray;
import static ee.carlrobert.openai.util.JSONUtil.jsonMap;
import static ee.carlrobert.openai.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.http.LocalCallbackServer;
import ee.carlrobert.openai.http.exchange.StreamHttpExchange;
import ee.carlrobert.openai.http.expectation.StreamExpectation;
import java.util.List;
import java.util.Map;

public class RequestHandlerTest extends BasePlatformTestCase {

  private LocalCallbackServer server;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    var settings = SettingsState.getInstance();
    settings.useApiKeyFromEnvVar = false;
    settings.apiKey = "TEST_API_KEY";
    settings.customHost = "http://localhost:8000";
    server = new LocalCallbackServer();
  }

  @Override
  protected void tearDown() throws Exception {
    server.stop();
    super.tearDown();
  }

  public void testChatCompletionCall() {
    var conversation = ConversationsState.getInstance().startConversation();
    var settings = SettingsState.getInstance();
    settings.isTextCompletionOptionSelected = false;
    settings.isChatCompletionOptionSelected = true;
    settings.useOpenAIService = true;
    settings.useAzureService = false;
    expectStreamRequest("/v1/chat/completions", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get(AUTHORIZATION).get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getBody())
          .extracting(
              "model",
              "messages")
          .containsExactly(
              "gpt-3.5-turbo",
              List.of(
                  Map.of("role", "system", "content",
                      "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."),
                  Map.of("role", "user", "content", "TEST_PROMPT")));

      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new RequestHandler(conversation).call(new Message("TEST_PROMPT"), false);

    await().atMost(5, SECONDS).until(() -> {
      var messages = conversation.getMessages();
      return !messages.isEmpty() && "Hello!".contentEquals(messages.get(0).getResponse());
    });
  }

  public void testTextCompletionCall() {
    var conversation = ConversationsState.getInstance().startConversation();
    var settings = SettingsState.getInstance();
    settings.isTextCompletionOptionSelected = true;
    settings.isChatCompletionOptionSelected = false;
    settings.useOpenAIService = true;
    settings.useAzureService = false;
    settings.textCompletionBaseModel = TextCompletionModel.CURIE.getCode();
    settings.organization = "TEST_ORGANIZATION";
    expectStreamRequest("/v1/completions", request -> {
      var headers = request.getHeaders();
      assertThat(headers.get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(headers.get("Openai-organization").get(0)).isEqualTo("TEST_ORGANIZATION");
      assertThat(request.getBody())
          .extracting(
              "model",
              "prompt")
          .containsExactly(
              "text-curie-001",
              "The following is a conversation with an AI assistant. "
                  + "The assistant is helpful, creative, clever, and very friendly.\n\n"
                  + "Human: TEST_PROMPT\n"
                  + "AI: \n");
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("text", "He"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "llo"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "!"))));
    });

    new RequestHandler(conversation).call(new Message("TEST_PROMPT"), false);

    await().atMost(5, SECONDS).until(() -> {
      var messages = conversation.getMessages();
      return !messages.isEmpty() && "Hello!".contentEquals(messages.get(0).getResponse());
    });
  }

  public void testAzureChatCompletionCall() {
    var settings = SettingsState.getInstance();
    settings.isTextCompletionOptionSelected = false;
    settings.isChatCompletionOptionSelected = true;
    settings.useOpenAIService = false;
    settings.useAzureService = true;
    settings.resourceName = "TEST_RESOURCE_NAME";
    settings.apiVersion = "TEST_API_VERSION";
    settings.deploymentId = "TEST_DEPLOYMENT_ID"; // TODO: Add support for asserting the host
    settings.chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();
    var conversationState = ConversationsState.getInstance();
    var conversation = conversationState.startConversation();
    var prevMessage = new Message("TEST_PREV_PROMPT");
    prevMessage.setResponse("TEST_PREV_RESPONSE");
    conversation.addMessage(prevMessage);
    conversationState.saveConversation(conversation);
    expectStreamRequest("/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions", request -> {
      assertThat(request.getUri().getQuery()).isEqualTo("api-version=TEST_API_VERSION");
      assertThat(request.getHeaders().get("Api-key").get(0)).isEqualTo("TEST_API_KEY");
      assertThat(request.getBody())
          .extracting(
              "model",
              "messages")
          .containsExactly(
              "gpt-3.5-turbo",
              List.of(
                  Map.of("role", "system", "content",
                      "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."),
                  Map.of("role", "user", "content", "TEST_PREV_PROMPT"),
                  Map.of("role", "assistant", "content", "TEST_PREV_RESPONSE"),
                  Map.of("role", "user", "content", "TEST_PROMPT")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new RequestHandler(conversation).call(new Message("TEST_PROMPT"), false);

    var messages = conversation.getMessages();
    await().atMost(5, SECONDS)
        .until(() -> messages.size() == 2 && messages.get(1).getResponse() != null);
    assertThat(messages)
        .extracting("prompt", "response")
        .containsExactly(
            tuple("TEST_PREV_PROMPT", "TEST_PREV_RESPONSE"),
            tuple("TEST_PROMPT", "Hello!"));
  }

  private void expectStreamRequest(String path, StreamHttpExchange exchange) {
    server.addExpectation(new StreamExpectation(path, exchange));
  }
}
