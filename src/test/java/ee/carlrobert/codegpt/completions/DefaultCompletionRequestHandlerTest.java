package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.openai.util.JSONUtil.jsonArray;
import static ee.carlrobert.openai.util.JSONUtil.jsonMap;
import static ee.carlrobert.openai.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.http.LocalCallbackServer;
import ee.carlrobert.openai.http.exchange.StreamHttpExchange;
import ee.carlrobert.openai.http.expectation.StreamExpectation;
import java.util.List;
import java.util.Map;

public class DefaultCompletionRequestHandlerTest extends BasePlatformTestCase {

  private LocalCallbackServer server;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    AzureCredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    OpenAICredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    SettingsState.getInstance().openAIBaseHost = "http://127.0.0.1:8000";
    SettingsState.getInstance().azureBaseHost = "http://127.0.0.1:8000";
    ConfigurationState.getInstance().systemPrompt = "";
    server = new LocalCallbackServer(8000);
  }

  @Override
  protected void tearDown() throws Exception {
    server.stop();
    super.tearDown();
  }

  public void testChatCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var requestHandler = new CompletionRequestHandler(getProject());
    requestHandler.addRequestCompletedListener(message::setResponse);
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
                  Map.of("role", "system", "content", COMPLETION_SYSTEM_PROMPT),
                  Map.of("role", "user", "content", "TEST_PROMPT")));

      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    requestHandler.call(conversation, message, false);

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testTextCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var requestHandler = new CompletionRequestHandler(getProject());
    requestHandler.addRequestCompletedListener(message::setResponse);
    var settings = SettingsState.getInstance();
    settings.isTextCompletionOptionSelected = true;
    settings.isChatCompletionOptionSelected = false;
    settings.useOpenAIService = true;
    settings.useAzureService = false;
    settings.textCompletionBaseModel = TextCompletionModel.CURIE.getCode();
    settings.openAIOrganization = "TEST_ORGANIZATION";
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

    requestHandler.call(conversation, message, false);

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testAzureChatCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var settings = SettingsState.getInstance();
    settings.isTextCompletionOptionSelected = false;
    settings.isChatCompletionOptionSelected = true;
    settings.useOpenAIService = false;
    settings.useAzureService = true;
    settings.azureResourceName = "TEST_RESOURCE_NAME";
    settings.azureApiVersion = "TEST_API_VERSION";
    settings.azureDeploymentId = "TEST_DEPLOYMENT_ID"; // TODO: Add support for asserting the host
    settings.chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();
    var conversationService = ConversationService.getInstance();
    var conversation = conversationService.startConversation();
    var requestHandler = new CompletionRequestHandler(getProject());
    requestHandler.addRequestCompletedListener(message::setResponse);
    var prevMessage = new Message("TEST_PREV_PROMPT");
    prevMessage.setResponse("TEST_PREV_RESPONSE");
    conversation.addMessage(prevMessage);
    conversationService.saveConversation(conversation);
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
                  Map.of("role", "system", "content", COMPLETION_SYSTEM_PROMPT),
                  Map.of("role", "user", "content", "TEST_PREV_PROMPT"),
                  Map.of("role", "assistant", "content", "TEST_PREV_RESPONSE"),
                  Map.of("role", "user", "content", "TEST_PROMPT")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    requestHandler.call(conversation, message, false);

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  private void expectStreamRequest(String path, StreamHttpExchange exchange) {
    server.addExpectation(new StreamExpectation(path, exchange));
  }
}
