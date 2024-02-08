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

import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.llm.client.http.exchange.NdJsonStreamHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import java.util.List;
import java.util.Map;
import testsupport.IntegrationTest;

public class DefaultCompletionRequestHandlerTest extends IntegrationTest {

  public void testOpenAIChatCompletionCall() {
    useOpenAIService();
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));
    expectOpenAI((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get(AUTHORIZATION).get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getBody())
          .extracting(
              "model",
              "messages")
          .containsExactly(
              "gpt-4",
              List.of(
                  Map.of("role", "system", "content", COMPLETION_SYSTEM_PROMPT),
                  Map.of("role", "user", "content", "TEST_PROMPT")));

      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testAzureChatCompletionCall() {
    useAzureService();
    var conversationService = ConversationService.getInstance();
    var prevMessage = new Message("TEST_PREV_PROMPT");
    prevMessage.setResponse("TEST_PREV_RESPONSE");
    var conversation = conversationService.startConversation();
    conversation.addMessage(prevMessage);
    conversationService.saveConversation(conversation);
    expectAzure((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions");
      assertThat(request.getUri().getQuery()).isEqualTo("api-version=TEST_API_VERSION");
      assertThat(request.getHeaders().get("Api-key").get(0)).isEqualTo("TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0)).isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting("messages")
          .isEqualTo(
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
    var message = new Message("TEST_PROMPT");
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testAzureChatCompletionCallWithCustomSettings() {
    useAzureService();
    AzureSettings.getInstance()
        .getState()
        .setPath("/codegpt/deployments/%s/completions?api-version=%s");
    var conversationService = ConversationService.getInstance();
    var prevMessage = new Message("TEST_PREV_PROMPT");
    prevMessage.setResponse("TEST_PREV_RESPONSE");
    var conversation = conversationService.startConversation();
    conversation.addMessage(prevMessage);
    conversationService.saveConversation(conversation);
    expectAzure((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath())
          .isEqualTo("/codegpt/deployments/TEST_DEPLOYMENT_ID/completions");
      assertThat(request.getUri().getQuery()).isEqualTo("api-version=TEST_API_VERSION");
      assertThat(request.getHeaders().get("Api-key").get(0)).isEqualTo("TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0)).isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting("messages")
          .isEqualTo(
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
    var message = new Message("TEST_PROMPT");
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testYouChatCompletionCall() {
    useYouService();
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("Ping", "Pong"));
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));
    expectYou((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/streamingSearch");
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getUri().getPath()).isEqualTo("/api/streamingSearch");
      assertThat(request.getUri().getQuery()).isEqualTo(
          "q=TEST_PROMPT&"
              + "page=1&"
              + "cfr=CodeGPT&"
              + "count=10&"
              + "safeSearch=WebPages,Translations,TimeZone,Computation,RelatedSearches&"
              + "domain=youchat&"
              + "chat=[{\"question\":\"Ping\",\"answer\":\"Pong\"}]&"
              + "utm_source=ide&"
              + "utm_medium=jetbrains&"
              + "utm_campaign=" + CodeGPTPlugin.getVersion() + "&"
              + "utm_content=CodeGPT");
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection", "User-agent", "Cookie")
          .containsExactly(
              "text/event-stream",
              "Keep-Alive",
              "youide CodeGPT",
              "safesearch_guest=Moderate; "
                  + "youpro_subscription=true; "
                  + "you_subscription=free; "
                  + "stytch_session=; "
                  + "ydc_stytch_session=; "
                  + "stytch_session_jwt=; "
                  + "ydc_stytch_session_jwt=; "
                  + "eg4=false; "
                  + "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-"
                  + "AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8"
                  + "SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;");
      return List.of(
          jsonMapResponse("youChatToken", "Hel"),
          jsonMapResponse("youChatToken", "lo"),
          jsonMapResponse("youChatToken", "!"));
    });

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testLlamaChatCompletionCall() {
    useLlamaService();
    ConfigurationSettings.getCurrentState().setMaxTokens(99);
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("Ping", "Pong"));
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
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
              99,
              true);
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo!"),
          jsonMapResponse(
              e("content", ""),
              e("stop", true)));
    });

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  public void testOllamaChatCompletionCall() {
    useOllamaService();
    ConfigurationSettings.getCurrentState().setMaxTokens(99);
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("Ping", "Pong"));
    var requestHandler = new CompletionRequestHandler(false, getRequestEventListener(message));
    expectOllama((NdJsonStreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/generate");
      assertThat(request.getBody())
          .extracting("model", "prompt", "options.num_predict", "stream")
          .containsExactly(
              HuggingFaceModel.CODE_LLAMA_7B_Q4.getOllamaTag(),
              LLAMA.buildPrompt(
                  COMPLETION_SYSTEM_PROMPT,
                  "TEST_PROMPT",
                  conversation.getMessages()),
              99,
              true);
      return List.of(
          jsonMapResponse(e("response", "Hel"), e("done", false)),
          jsonMapResponse(e("response", "lo!"), e("done", true)));
    });

    requestHandler.call(new CallParameters(conversation, ConversationType.DEFAULT, message, false));

    await().atMost(5, SECONDS).until(() -> "Hello!".equals(message.getResponse()));
  }

  private CompletionResponseEventListener getRequestEventListener(Message message) {
    return new CompletionResponseEventListener() {
      @Override
      public void handleCompleted(String fullMessage, CallParameters callParameters) {
        message.setResponse(fullMessage);
      }
    };
  }
}
