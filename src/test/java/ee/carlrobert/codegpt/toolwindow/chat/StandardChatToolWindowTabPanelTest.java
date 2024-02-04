package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.FIX_COMPILE_ERRORS_SYSTEM_PROMPT;
import static ee.carlrobert.codegpt.completions.PromptTemplate.LLAMA;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.LlamaCppSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import ee.carlrobert.embedding.ReferencedFile;
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
            encodingManager.countTokens(message.getPrompt()),
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

  public void testSendingOpenAIMessageWithReferencedContext() {
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, List.of(
        new ReferencedFile("TEST_FILE_NAME_1", "TEST_FILE_PATH_1", "TEST_FILE_CONTENT_1"),
        new ReferencedFile("TEST_FILE_NAME_2", "TEST_FILE_PATH_2", "TEST_FILE_CONTENT_2"),
        new ReferencedFile("TEST_FILE_NAME_3", "TEST_FILE_PATH_3", "TEST_FILE_CONTENT_3")));
    useOpenAIService();
    ConfigurationState.getInstance().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    var message = new Message("TEST_MESSAGE");
    message.setUserMessage("TEST_MESSAGE");
    message.setReferencedFilePaths(
        List.of("TEST_FILE_PATH_1", "TEST_FILE_PATH_2", "TEST_FILE_PATH_3"));
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
                  Map.of("role", "user", "content",
                      "Use the following context to answer question at the end:\n\n"
                          + "File Path: TEST_FILE_PATH_1\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_1\n"
                          + "TEST_FILE_CONTENT_1\n"
                          + "```\n\n"
                          + "File Path: TEST_FILE_PATH_2\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_2\n"
                          + "TEST_FILE_CONTENT_2\n"
                          + "```\n\n"
                          + "File Path: TEST_FILE_PATH_3\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_3\n"
                          + "TEST_FILE_CONTENT_3\n"
                          + "```\n\n"
                          + "Question: TEST_MESSAGE")));
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
            encodingManager.countTokens(message.getPrompt()),
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
        .extracting("id", "prompt", "response", "referencedFilePaths")
        .containsExactly(
            message.getId(),
            message.getPrompt(),
            message.getResponse(),
            List.of("TEST_FILE_PATH_1", "TEST_FILE_PATH_2", "TEST_FILE_PATH_3"));
  }

  public void testFixCompileErrorsWithOpenAIService() {
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, List.of(
        new ReferencedFile("TEST_FILE_NAME_1", "TEST_FILE_PATH_1", "TEST_FILE_CONTENT_1"),
        new ReferencedFile("TEST_FILE_NAME_2", "TEST_FILE_PATH_2", "TEST_FILE_CONTENT_2"),
        new ReferencedFile("TEST_FILE_NAME_3", "TEST_FILE_PATH_3", "TEST_FILE_CONTENT_3")));
    useOpenAIService();
    ConfigurationState.getInstance().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    var message = new Message("TEST_MESSAGE");
    message.setUserMessage("TEST_MESSAGE");
    message.setReferencedFilePaths(
        List.of("TEST_FILE_PATH_1", "TEST_FILE_PATH_2", "TEST_FILE_PATH_3"));
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
                  Map.of("role", "system", "content", FIX_COMPILE_ERRORS_SYSTEM_PROMPT),
                  Map.of("role", "user", "content",
                      "Use the following context to answer question at the end:\n\n"
                          + "File Path: TEST_FILE_PATH_1\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_1\n"
                          + "TEST_FILE_CONTENT_1\n"
                          + "```\n\n"
                          + "File Path: TEST_FILE_PATH_2\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_2\n"
                          + "TEST_FILE_CONTENT_2\n"
                          + "```\n\n"
                          + "File Path: TEST_FILE_PATH_3\n"
                          + "File Content:\n"
                          + "```TEST_FILE_NAME_3\n"
                          + "TEST_FILE_CONTENT_3\n"
                          + "```\n\n"
                          + "Question: TEST_MESSAGE")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    panel.sendMessage(message, ConversationType.FIX_COMPILE_ERRORS);

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
            encodingManager.countTokens(message.getPrompt()),
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
        .extracting("id", "prompt", "response", "referencedFilePaths")
        .containsExactly(
            message.getId(),
            message.getPrompt(),
            message.getResponse(),
            List.of("TEST_FILE_PATH_1", "TEST_FILE_PATH_2", "TEST_FILE_PATH_3"));
  }

  public void testSendingLlamaMessage() {
    useLlamaService();
    var configurationState = ConfigurationState.getInstance();
    configurationState.setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    configurationState.setMaxTokens(1000);
    configurationState.setTemperature(0.1);
    var llamaSettings = LlamaCppSettingsState.getInstance();
    LlamaLocalSettings localSettings = llamaSettings.getLocalSettings();
    localSettings.setModel(HuggingFaceModel.CODE_LLAMA_7B_Q4);
    LlamaRequestSettings llamaRequestSettings = llamaSettings.getRequestSettings();
    llamaRequestSettings.setTopK(30);
    llamaRequestSettings.setTopP(0.8);
    llamaRequestSettings.setMinP(0.03);
    llamaRequestSettings.setRepeatPenalty(1.3);
    var message = new Message("TEST_PROMPT");
    var conversation = ConversationService.getInstance().startConversation();
    var panel = new StandardChatToolWindowTabPanel(getProject(), conversation);
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getBody())
          .extracting(
              "prompt",
              "n_predict",
              "stream",
              "temperature",
              "top_k",
              "top_p",
              "min_p",
              "repeat_penalty")
          .containsExactly(
              LLAMA.buildPrompt(
                  COMPLETION_SYSTEM_PROMPT,
                  "TEST_PROMPT",
                  conversation.getMessages()),
              configurationState.getMaxTokens(),
              true,
              configurationState.getTemperature(),
              llamaRequestSettings.getTopK(),
              llamaRequestSettings.getTopP(),
              llamaRequestSettings.getMinP(),
              llamaRequestSettings.getRepeatPenalty());
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo!"),
          jsonMapResponse(
              e("content", ""),
              e("stop", true)));
    });

    panel.sendMessage(message, ConversationType.DEFAULT);

    await().atMost(5, SECONDS)
        .until(() -> {
          var messages = conversation.getMessages();
          return !messages.isEmpty() && "Hello!".equals(messages.get(0).getResponse());
        });
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
