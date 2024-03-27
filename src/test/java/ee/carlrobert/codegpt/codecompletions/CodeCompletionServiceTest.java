package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.PREVIOUS_INLAY_TEXT;
import static ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate.LLAMA;
import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.editor.VisualPosition;
import com.intellij.testFramework.PlatformTestUtil;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import java.util.List;
import testsupport.IntegrationTest;

public class CodeCompletionServiceTest extends IntegrationTest {

  private final VisualPosition cursorPosition = new VisualPosition(3, 0);

  public void testFetchCodeCompletionLlama() {
    useLlamaService();
    ConfigurationSettings.getCurrentState().setCodeCompletionsEnabled(true);
    myFixture.configureByText(
        "CompletionTest.java",
        getResourceContent("/codecompletions/code-completion-file.txt"));
    myFixture.getEditor().getCaretModel().moveToVisualPosition(cursorPosition);
    var expectedCompletion = "TEST_OUTPUT";
    var prefix = "z".repeat(245) + "\n[INPUT]\nc"; // 128 tokens
    var suffix = "\n[\\INPUT]\n" + "z".repeat(247); // 128 tokens
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("prompt")
          .isEqualTo(LLAMA.buildPrompt(prefix, suffix));
      return List.of(jsonMapResponse(e("content", expectedCompletion), e("stop", true)));
    });

    myFixture.type('c');

    PlatformTestUtil.waitWithEventsDispatching(
        "Editor inlay assertions failed",
        () -> "TEST_OUTPUT".equals(PREVIOUS_INLAY_TEXT.get(myFixture.getEditor())),
        5);
  }
}
