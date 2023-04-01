package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Color;

public class ThemeUtils {

  public static String getBackgroundColorRGB() {
    return getRGB(UIUtil.getPanelBackground());
  }

  public static String getFontColorRGB() {
    return getRGB(EditorColorsManager.getInstance().getSchemeForCurrentUITheme().getDefaultForeground());
  }

  public static String getSeparatorColorRGB() {
    return getRGB(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground());
  }

  public static String getPanelBackgroundColorRGB() {
    var panelBg = UIUtil.getPanelBackground();
    return getRGB(UIUtil.isUnderDarcula() ? toDarker(panelBg) : panelBg.brighter());
  }

  public static int getFontSize() {
    return JBFont.regular().getSize();
  }

  private static Color toDarker(Color color) {
    var factor = 0.9;
    return new Color(
        Math.max((int) (color.getRed() * factor), 0),
        Math.max((int) (color.getGreen() * factor), 0),
        Math.max((int) (color.getBlue() * factor), 0),
        color.getAlpha());
  }

  private static String getRGB(Color color) {
    return format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue());
  }
}
