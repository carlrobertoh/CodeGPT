package ee.carlrobert.codegpt.client;

import static ee.carlrobert.openai.util.JSONUtil.jsonArray;
import static ee.carlrobert.openai.util.JSONUtil.jsonMap;
import static ee.carlrobert.openai.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.advanced.AdvancedSettingsState;
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
    SettingsState.getInstance().apiKey = "TEST_API_KEY";
    AdvancedSettingsState.getInstance().host = "http://localhost:8000";
    server = new LocalCallbackServer();
  }

  @Override
  protected void tearDown() throws Exception {
    server.stop();
    super.tearDown();
  }

  public void testChatCompletionCall() {
    var conversation = ConversationsState.getInstance().startConversation();
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
    settings.textCompletionBaseModel = TextCompletionModel.CURIE.getCode();
    expectStreamRequest("/v1/completions", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
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

  private void expectStreamRequest(String path, StreamHttpExchange exchange) {
    server.addExpectation(new StreamExpectation(path, exchange));
  }
}
