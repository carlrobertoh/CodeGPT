package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jetbrains.annotations.NotNull;

public class InlayInlineElementRenderer implements EditorCustomElementRenderer {

  private final String inlayText;

  public InlayInlineElementRenderer(String inlayText) {
    this.inlayText = inlayText;
  }

  @Override
  public int calcWidthInPixels(@NotNull Inlay inlay) {
    var editor = inlay.getEditor();
    return editor.getContentComponent()
        .getFontMetrics(editor.getColorsScheme().getFont(EditorFontType.PLAIN))
        .stringWidth(inlayText);
  }

  @Override
  public void paint(
      @NotNull Inlay inlay,
      @NotNull Graphics2D g,
      @NotNull Rectangle2D targetRegion,
      @NotNull TextAttributes textAttributes) {
    Editor editor = inlay.getEditor();
    Font font = editor.getColorsScheme()
        .getFont(EditorFontType.PLAIN)
        .deriveFont(Font.ITALIC);
    g.setFont(font);
    g.setColor(JBColor.GRAY);
    g.drawString(
        inlayText,
        (int) targetRegion.getX(),
        (int) targetRegion.getY() + editor.getAscent());
  }
}
