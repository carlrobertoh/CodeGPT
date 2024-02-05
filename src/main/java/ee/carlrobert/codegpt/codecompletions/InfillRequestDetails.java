package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import ee.carlrobert.codegpt.EncodingManager;

public class InfillRequestDetails {

  private static final int MAX_OFFSET = 10_000;
  private static final int MAX_PROMPT_TOKENS = 512;

  private final String prefix;
  private final String suffix;

  public InfillRequestDetails(String prefix, String suffix) {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public static InfillRequestDetails fromDocumentWithMaxOffset(Document document, int caretOffset) {
    int start = Math.max(0, caretOffset - MAX_OFFSET);
    int end = Math.min(document.getTextLength(), caretOffset + MAX_OFFSET);
    return fromDocumentWithCustomRange(document, caretOffset, start, end);
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  private static InfillRequestDetails fromDocumentWithCustomRange(
      Document document,
      int caretOffset,
      int start,
      int end) {
    var prefix = truncateText(document, start, caretOffset, false);
    var suffix = truncateText(document, caretOffset, end, true);
    return new InfillRequestDetails(prefix, suffix);
  }

  private static String truncateText(Document document, int start, int end, boolean fromStart) {
    return EncodingManager.getInstance().truncateText(
        document.getText(new TextRange(start, end)),
        MAX_PROMPT_TOKENS,
        fromStart);
  }
}
