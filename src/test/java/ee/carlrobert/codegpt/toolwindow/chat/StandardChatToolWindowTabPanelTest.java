package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.FIX_COMPILE_ERRORS_SYSTEM_PROMPT;
import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.PlatformTestUtil;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import testsupport.IntegrationTest;

public class StandardChatToolWindowTabPanelTest extends IntegrationTest {

  public void testSendingOpenAIMessage() {
    useOpenAIService();
    ConfigurationSettings.getCurrentState().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
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

    waitExpecting(() -> {
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
    ConfigurationSettings.getCurrentState().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
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
                      """
                          Use the following context to answer question at the end:

                          File Path: TEST_FILE_PATH_1
                          File Content:
                          ```TEST_FILE_NAME_1
                          TEST_FILE_CONTENT_1
                          ```

                          File Path: TEST_FILE_PATH_2
                          File Content:
                          ```TEST_FILE_NAME_2
                          TEST_FILE_CONTENT_2
                          ```

                          File Path: TEST_FILE_PATH_3
                          File Content:
                          ```TEST_FILE_NAME_3
                          TEST_FILE_CONTENT_3
                          ```

                          Question: TEST_MESSAGE""")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    panel.sendMessage(message);

    waitExpecting(() -> {
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

  public void testSendingOpenAIMessageWithImage() {
    var testImagePath = requireNonNull(getClass().getResource("/images/test-image.png")).getPath();
    getProject().putUserData(CodeGPTKeys.UPLOADED_FILE_PATH, testImagePath);
    useOpenAIService("gpt-4-vision-preview");
    ConfigurationSettings.getCurrentState().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    var message = new Message("TEST_MESSAGE");
    var conversation = ConversationService.getInstance().startConversation();
    var panel = new StandardChatToolWindowTabPanel(getProject(), conversation);
    expectOpenAI((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get(AUTHORIZATION).get(0)).isEqualTo("Bearer TEST_API_KEY");
      try {
        var testImageUrl = "data:image/png;base64,"
            + Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(testImagePath)));
        assertThat(request.getBody())
            .extracting("model", "messages")
            .containsExactly(
                "gpt-4-vision-preview",
                List.of(
                    Map.of("role", "system", "content", COMPLETION_SYSTEM_PROMPT),
                    Map.of("role", "user", "content", List.of(
                        Map.of(
                            "type", "image_url",
                            "image_url", Map.of("url", testImageUrl)),
                        Map.of("type", "text", "text", "TEST_MESSAGE")
                    ))));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    panel.sendMessage(message);

    waitExpecting(() -> {
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
        .extracting("id", "prompt", "response", "imageFilePath")
        .containsExactly(
            message.getId(),
            message.getPrompt(),
            message.getResponse(),
            message.getImageFilePath());
  }

  public void testFixCompileErrorsWithOpenAIService() {
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, List.of(
        new ReferencedFile("TEST_FILE_NAME_1", "TEST_FILE_PATH_1", "TEST_FILE_CONTENT_1"),
        new ReferencedFile("TEST_FILE_NAME_2", "TEST_FILE_PATH_2", "TEST_FILE_CONTENT_2"),
        new ReferencedFile("TEST_FILE_NAME_3", "TEST_FILE_PATH_3", "TEST_FILE_CONTENT_3")));
    useOpenAIService();
    ConfigurationSettings.getCurrentState().setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
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
                      """
                          Use the following context to answer question at the end:

                          File Path: TEST_FILE_PATH_1
                          File Content:
                          ```TEST_FILE_NAME_1
                          TEST_FILE_CONTENT_1
                          ```

                          File Path: TEST_FILE_PATH_2
                          File Content:
                          ```TEST_FILE_NAME_2
                          TEST_FILE_CONTENT_2
                          ```

                          File Path: TEST_FILE_PATH_3
                          File Content:
                          ```TEST_FILE_NAME_3
                          TEST_FILE_CONTENT_3
                          ```

                          Question: TEST_MESSAGE""")));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    panel.sendMessage(message, ConversationType.FIX_COMPILE_ERRORS);

    waitExpecting(() -> {
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
    var configurationState = ConfigurationSettings.getCurrentState();
    configurationState.setSystemPrompt(COMPLETION_SYSTEM_PROMPT);
    configurationState.setMaxTokens(1000);
    configurationState.setTemperature(0.1);
    var llamaSettings = LlamaSettings.getCurrentState();
    llamaSettings.setUseCustomModel(false);
    llamaSettings.setHuggingFaceModel(HuggingFaceModel.CODE_LLAMA_7B_Q4);
    llamaSettings.setTopK(30);
    llamaSettings.setTopP(0.8);
    llamaSettings.setMinP(0.03);
    llamaSettings.setRepeatPenalty(1.3);
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
              llamaSettings.getTopK(),
              llamaSettings.getTopP(),
              llamaSettings.getMinP(),
              llamaSettings.getRepeatPenalty());
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo!"),
          jsonMapResponse(
              e("content", ""),
              e("stop", true)));
    });

    panel.sendMessage(message, ConversationType.DEFAULT);

    waitExpecting(() -> {
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
