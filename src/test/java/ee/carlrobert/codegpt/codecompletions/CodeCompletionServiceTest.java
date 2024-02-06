package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static ee.carlrobert.codegpt.codecompletions.CodeCompletionService.APPLY_INLAY_ACTION_ID;
import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.PlatformTestUtil;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import java.util.List;
import testsupport.IntegrationTest;

public class CodeCompletionServiceTest extends IntegrationTest {

  private final VisualPosition cursorPosition = new VisualPosition(3, 0);

  public void testFetchCodeCompletionLlama() {
    useLlamaService();
    ConfigurationState.getInstance().setCodeCompletionsEnabled(true);
    myFixture.configureByText(
        "CompletionTest.java",
        getResourceContent("/codecompletions/code-completion-file.txt"));
    Editor editor = myFixture.getEditor();
    var expectedCompletion = "TEST_SINGLE_LINE_OUTPUT\nTEST_MULTI_LINE_OUTPUT";
    var prefix = "z".repeat(1015) + "\n[INPUT]\n"; // 512 tokens
    var suffix = "\n[\\INPUT]\n" + "z".repeat(1015); // 512 tokens
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("prompt")
          .isEqualTo(InfillPromptTemplate.LLAMA.buildPrompt(prefix, suffix));
      return List.of(jsonMapResponse(e("content", expectedCompletion), e("stop", true)));
    });

    editor.getCaretModel().moveToVisualPosition(cursorPosition);

    PlatformTestUtil.waitWithEventsDispatching(
        "Editor inlay assertions failed",
        () -> {
          var singleLineInlayElement = editor.getUserData(SINGLE_LINE_INLAY);
          var multiLineInlayElement = editor.getUserData(MULTI_LINE_INLAY);
          if (singleLineInlayElement != null && multiLineInlayElement != null) {
            var singleLine =
                ((InlayInlineElementRenderer) singleLineInlayElement.getRenderer())
                    .getInlayText();
            var multiLine =
                ((InlayBlockElementRenderer) multiLineInlayElement.getRenderer()).getInlayText();
            return "TEST_SINGLE_LINE_OUTPUT".equals(singleLine)
                && "TEST_MULTI_LINE_OUTPUT".equals(multiLine);
          }
          return false;
        }, 5);
  }

  public void testApplyInlayAction() {
    ConfigurationState.getInstance().setAutoFormattingEnabled(false);
    myFixture.configureByText(
        "CompletionTest.java",
        getResourceContent("/codecompletions/code-completion-file.txt"));
    var editor = myFixture.getEditor();
    editor.getCaretModel().moveToVisualPosition(cursorPosition);
    var expectedSingleLineInlay = "FIRST_LINE";
    var expectedMultiLineInlay = "SECOND_LINE\nTHIRD_LINE";
    var expectedInlay = expectedSingleLineInlay + "\n" + expectedMultiLineInlay;
    int cursorOffsetBeforeApply = editor.getCaretModel().getOffset();
    CodeCompletionService.getInstance(getProject())
        .addInlays(editor, cursorOffsetBeforeApply, expectedInlay);

    myFixture.performEditorAction(APPLY_INLAY_ACTION_ID);

    var newTextRange = new TextRange(cursorOffsetBeforeApply, editor.getCaretModel().getOffset());
    var appliedInlay = editor.getDocument().getText(newTextRange);
    assertThat(appliedInlay).isEqualTo(expectedInlay);
  }
}
