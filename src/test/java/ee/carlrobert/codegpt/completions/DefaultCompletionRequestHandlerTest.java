package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.llm.client.http.LocalCallbackServer;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.http.expectation.StreamExpectation;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import java.util.List;
import java.util.Map;

public class DefaultCompletionRequestHandlerTest extends BasePlatformTestCase {

  private LocalCallbackServer server;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    AzureCredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    OpenAICredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    OpenAISettingsState.getInstance().setBaseHost("http://127.0.0.1:8000");
    AzureSettingsState.getInstance().setBaseHost("http://127.0.0.1:8000");
    ConfigurationState.getInstance().setSystemPrompt("");
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
    var requestHandler = new CompletionRequestHandler();
    requestHandler.addRequestCompletedListener(message::setResponse);
    var settings = SettingsState.getInstance();
    settings.setUseOpenAIService(true);
    settings.setUseAzureService(false);
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

  public void testAzureChatCompletionCall() {
    AzureSettingsState.getInstance().setModel(OpenAIChatCompletionModel.GPT_3_5.getCode());
    var settings = SettingsState.getInstance();
    settings.setUseOpenAIService(false);
    settings.setUseAzureService(true);
    var azureSettings = AzureSettingsState.getInstance();
    azureSettings.setResourceName("TEST_RESOURCE_NAME");
    azureSettings.setApiVersion("TEST_API_VERSION");
    azureSettings.setDeploymentId("TEST_DEPLOYMENT_ID");
    var conversationService = ConversationService.getInstance();
    var requestHandler = new CompletionRequestHandler();
    var message = new Message("TEST_PROMPT");
    requestHandler.addRequestCompletedListener(message::setResponse);
    var prevMessage = new Message("TEST_PREV_PROMPT");
    prevMessage.setResponse("TEST_PREV_RESPONSE");
    var conversation = conversationService.startConversation();
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
