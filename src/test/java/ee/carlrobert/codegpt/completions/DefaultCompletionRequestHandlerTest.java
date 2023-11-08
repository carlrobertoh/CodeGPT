package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
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
    // FIXME
    OpenAISettingsState.getInstance().setBaseHost("http://127.0.0.1:8000");
    AzureSettingsState.getInstance().setBaseHost("http://127.0.0.1:8000");
    YouSettingsState.getInstance().setBaseHost("http://127.0.0.1:8000");
    LlamaSettingsState.getInstance().setServerPort(8000);
    ConfigurationState.getInstance().setSystemPrompt("");
    server = new LocalCallbackServer(8000);
  }

  @Override
  protected void tearDown() throws Exception {
    server.stop();
    super.tearDown();
  }

  public void testOpenAIChatCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var requestHandler = new CompletionRequestHandler();
    requestHandler.addRequestCompletedListener(message::setResponse);
    SettingsState.getInstance().setSelectedService(ServiceType.OPENAI);
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
    SettingsState.getInstance().setSelectedService(ServiceType.AZURE);
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

  public void testYouChatCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("Ping", "Pong"));
    var requestHandler = new CompletionRequestHandler();
    requestHandler.addRequestCompletedListener(message::setResponse);
    SettingsState.getInstance().setSelectedService(ServiceType.YOU);
    expectStreamRequest("/api/streamingSearch", request -> {
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getUri().getPath()).isEqualTo("/api/streamingSearch");
      assertThat(request.getUri().getQuery()).isEqualTo(
          "q=TEST_PROMPT&" +
              "page=1&" +
              "cfr=CodeGPT&" +
              "count=10&" +
              "safeSearch=WebPages,Translations,TimeZone,Computation,RelatedSearches&" +
              "domain=youchat&" +
              "chat=[{\"question\":\"Ping\",\"answer\":\"Pong\"}]&" +
              "utm_source=ide&" +
              "utm_medium=jetbrains&" +
              "utm_campaign=" + CodeGPTPlugin.getVersion() + "&" +
              "utm_content=CodeGPT");
      assertThat(request.getHeaders())
          .flatExtracting("Host", "Accept", "Connection", "User-agent", "Cookie")
          .containsExactly("127.0.0.1:8000",
              "text/event-stream",
              "Keep-Alive",
              "youide CodeGPT",
              "safesearch_guest=Moderate; " +
                  "youpro_subscription=true; " +
                  "you_subscription=free; " +
                  "stytch_session=; " +
                  "ydc_stytch_session=; " +
                  "stytch_session_jwt=; " +
                  "ydc_stytch_session_jwt=; " +
                  "eg4=false; " +
                  "safesearch_9015f218b47611b62bbbaf61125cd2dac629e65c3d6f47573a2ec0e9b615c691=Moderate; "
                  +
                  "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;");
      return List.of(
          jsonMapResponse("youChatToken", "Hel"),
          jsonMapResponse("youChatToken", "lo"),
          jsonMapResponse("youChatToken", "!"));
    });

    requestHandler.call(conversation, message, false);

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testLlamaChatCompletionCall() {
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("Ping", "Pong"));
    var requestHandler = new CompletionRequestHandler();
    requestHandler.addRequestCompletedListener(message::setResponse);
    SettingsState.getInstance().setSelectedService(ServiceType.LLAMA_CPP);
    expectStreamRequest("/completion", request -> {
      assertThat(request.getBody())
          .extracting(
              "prompt",
              "n_predict",
              "stream")
          .containsExactly(
              LLAMA.buildPrompt(
                  COMPLETION_SYSTEM_PROMPT,
                  "TEST_PROMPT",
                  conversation.getMessages()),
              512,
              true);
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo!"),
          jsonMapResponse(
              e("content", ""),
              e("stop", true)));
    });

    requestHandler.call(conversation, message, false);

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  private void expectStreamRequest(String path, StreamHttpExchange exchange) {
    server.addExpectation(new StreamExpectation(path, exchange));
  }
}
