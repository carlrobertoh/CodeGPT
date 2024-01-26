package ee.carlrobert.codegpt.codecompletions;

import static ee.carlrobert.codegpt.CodeGPTKeys.MULTI_LINE_INLAY;
import static ee.carlrobert.codegpt.CodeGPTKeys.SINGLE_LINE_INLAY;
import static ee.carlrobert.codegpt.codecompletions.CodeCompletionService.APPLY_INLAY_ACTION_ID;
import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;
import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import testsupport.IntegrationTest;

public class CodeCompletionServiceTest extends IntegrationTest {

  private final VisualPosition cursorPosition = new VisualPosition(2, 8);

  public void testFetchCodeCompletionLlama() {
    useLlamaService();
    var codeCompletionService = CodeCompletionService.getInstance();
    String fileContents = getResourceContent(
        "/codecompletions/code-completion-file.txt");
    PsiFile psiFile = myFixture.configureByText("CompletionTest.java", fileContents);
    Editor editor = myFixture.getEditor();
    Document document = editor.getDocument();
    editor.getCaretModel().moveToVisualPosition(cursorPosition);
    var prefix = "public static int gcd(int x, int y){\n"
        + "";
    var suffix = "\n"
        + "    }";
    var expectedCompletion = "return xyz;";
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/infill");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting(
              "input_prefix",
              "input_suffix")
          .containsExactly(prefix, suffix);
      return List.of(jsonMapResponse(e("content", expectedCompletion), e("stop", true)));
    });

    int caretOffset = editor.getCaretModel().getOffset();
    PsiElement elementAtCaret = ReadAction.compute(() -> psiFile.findElementAt(caretOffset));

    StringBuilder actualCompletion = new StringBuilder();
    codeCompletionService.fetchCodeCompletion(elementAtCaret, caretOffset, document,
        new CompletionEventListener() {
          @Override
          public void onComplete(StringBuilder messageBuilder) {
            actualCompletion.append(messageBuilder);
          }
        });
    await().atMost(2, SECONDS)
        .until(() -> actualCompletion.length() > 0);
    assertEquals(expectedCompletion, actualCompletion.toString());
  }

  public void testAddInlaysSingleLine() {
    var codeCompletionService = setupTestCodeCompletion();
    Editor editor = myFixture.getEditor();
    editor.getCaretModel().moveToVisualPosition(cursorPosition);
    var expectedInlay = "        return xyz;";
    int caretOffset = editor.getCaretModel().getOffset();

    AtomicReference<Boolean> onApplyCalled = new AtomicReference<>(false);
    Runnable onApply = () -> onApplyCalled.set(true);

    codeCompletionService.addInlays(editor, caretOffset, expectedInlay, onApply);

    checkInlay(editor.getUserData(SINGLE_LINE_INLAY), InlayInlineElementRenderer.class,
        expectedInlay, caretOffset);
    checkPerformInlayAction(editor.getDocument(), cursorPosition.line, cursorPosition.line,
        expectedInlay);
    assertTrue(onApplyCalled.get());
    ActionManager.getInstance().unregisterAction(APPLY_INLAY_ACTION_ID);
  }

  public void testAddInlaysMultiLine() {
    var codeCompletionService = setupTestCodeCompletion();
    Editor editor = myFixture.getEditor();
    editor.getCaretModel().moveToVisualPosition(cursorPosition);
    var expectedInlay = "        int z = 1;\n        z = 2 + 3;\n        return xyz;";
    int caretOffset = editor.getCaretModel().getOffset();

    AtomicReference<Boolean> onApplyCalled = new AtomicReference<>(false);
    Runnable onApply = () -> onApplyCalled.set(true);

    codeCompletionService.addInlays(editor, caretOffset, expectedInlay, onApply);

    // First line of inlay
    checkInlay(editor.getUserData(SINGLE_LINE_INLAY), InlayInlineElementRenderer.class,
        expectedInlay.substring(0, expectedInlay.indexOf("\n")), caretOffset);
    // Other lines of inlay
    checkInlay(editor.getUserData(MULTI_LINE_INLAY), InlayBlockElementRenderer.class,
        expectedInlay.substring(expectedInlay.indexOf("\n") + 1), caretOffset);
    checkPerformInlayAction(editor.getDocument(), cursorPosition.line, cursorPosition.line + 2,
        expectedInlay);
    assertTrue(onApplyCalled.get());
    ActionManager.getInstance().unregisterAction(APPLY_INLAY_ACTION_ID);
  }

  private CodeCompletionService setupTestCodeCompletion() {
    useLlamaService();
    var codeCompletionService = CodeCompletionService.getInstance();
    String fileContents = getResourceContent(
        "/codecompletions/code-completion-file.txt");
    myFixture.configureByText("CompletionTest.java", fileContents);
    return codeCompletionService;
  }

  private void checkInlay(Inlay<EditorCustomElementRenderer> inlay,
      Class<? extends EditorCustomElementRenderer> clazz, String expectedText, int expectedOffset) {
    assertNotNull(inlay);
    assertTrue(clazz.isInstance(inlay.getRenderer()));
    InlayElementRenderer renderer = (InlayElementRenderer) inlay.getRenderer();
    assertEquals(expectedText, renderer.getInlayText());
    assertEquals(expectedOffset, inlay.getOffset());
  }

  private void checkPerformInlayAction(Document document, int startLine, int endLine,
      String expectedText) {
    AnAction applyInlayAction = ActionManager.getInstance().getAction(APPLY_INLAY_ACTION_ID);
    assertNotNull(applyInlayAction);
    myFixture.performEditorAction(APPLY_INLAY_ACTION_ID);

    TextRange inlayTextRange = new TextRange(document.getLineStartOffset(startLine),
        document.getLineEndOffset(endLine));
    assertEquals(expectedText, document.getText(inlayTextRange));
  }


}
