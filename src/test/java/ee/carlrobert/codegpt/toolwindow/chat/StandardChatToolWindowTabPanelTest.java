package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import java.util.List;
import java.util.Map;
import testsupport.IntegrationTest;

public class StandardChatToolWindowTabPanelTest extends IntegrationTest {

  public void testSendingOpenAIMessage() {
    useOpenAIService();
    ConfigurationState.getInstance().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    var message = new Message("Hello!");
    var conversation = ConversationService.getInstance().startConversation();
    var panel = new StandardChatToolWindowTabPanel(getProject(), conversation);
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
                  Map.of("role", "user", "content", "Hello!")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    panel.sendMessage(message);

    await().atMost(5, SECONDS)
        .until(() -> {
          var messages = conversation.getMessages();
          return !messages.isEmpty() && "Hello!".equals(messages.get(0).getResponse());
        });
    var encodingManager = EncodingManager.getInstance();
    assertThat(panel.getTokenDetails()).extracting(
            "systemPromptTokens",
            "conversationTokens",
            "userPromptTokens",
            "highlightedTokens")
        .containsExactly(
            encodingManager.countTokens(COMPLETION_SYSTEM_PROMPT),
            encodingManager.countConversationTokens(panel.getConversation()),
            0,
            0);
    assertThat(panel.getConversation())
        .isNotNull()
        .extracting("id", "model", "clientCode", "discardTokenLimit")
        .containsExactly(
            conversation.getId(),
            conversation.getModel(),
            conversation.getClientCode(),
            false);
    var messages = panel.getConversation().getMessages();
    assertThat(messages.size()).isOne();
    assertThat(messages.get(0))
        .extracting("id", "prompt", "response")
        .containsExactly(message.getId(), message.getPrompt(), message.getResponse());
  }
}
