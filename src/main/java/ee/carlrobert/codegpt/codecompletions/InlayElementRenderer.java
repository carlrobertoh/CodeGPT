package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

public abstract class InlayElementRenderer implements EditorCustomElementRenderer {

  protected final String inlayText;

  protected InlayElementRenderer(String inlayText) {
    this.inlayText = inlayText;
  }

  @Override
  public int calcWidthInPixels(@NotNull Inlay inlay) {
    var longestLine = getInlayText().lines()
        .max(Comparator.comparingInt(String::length))
        .orElse("");
    var editor = inlay.getEditor();
    return editor.getContentComponent()
        .getFontMetrics(editor.getColorsScheme().getFont(EditorFontType.PLAIN))
        .stringWidth(longestLine);
  }

  @Override
  public int calcHeightInPixels(@NotNull Inlay inlay) {
    int lineHeight = inlay.getEditor().getLineHeight();
    int linesCount = (int) inlayText.lines().count();
    return lineHeight * linesCount;
  }

  public String getInlayText() {
    return inlayText;
  }
}
