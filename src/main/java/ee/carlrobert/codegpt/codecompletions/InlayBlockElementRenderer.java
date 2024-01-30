package ee.carlrobert.codegpt.codecompletions;

import static java.util.stream.Collectors.toList;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.FontInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class InlayBlockElementRenderer extends InlayElementRenderer {

  protected InlayBlockElementRenderer(String inlayText) {
    super(inlayText);
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

    int x = (int) targetRegion.getX();
    int fontBaseLineOffset = (int) calculateFontBaseLineOffset(font, editor);
    List<String> lines = inlayText.lines().collect(toList());
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      int y = (int) targetRegion.getY() + fontBaseLineOffset + i * editor.getLineHeight();
      g.drawString(line, x, y);
    }
  }

  public double calculateFontBaseLineOffset(Font font, Editor editor) {
    FontRenderContext fontRenderContext =
        FontInfo.getFontRenderContext(editor.getContentComponent());
    Rectangle2D visualBounds = font.createGlyphVector(fontRenderContext, "Abc").getVisualBounds();
    double fontBaseline = visualBounds.getHeight();
    double linePadding = (editor.getLineHeight() - fontBaseline) / 2;
    return Math.ceil(fontBaseline + linePadding);
  }
}
