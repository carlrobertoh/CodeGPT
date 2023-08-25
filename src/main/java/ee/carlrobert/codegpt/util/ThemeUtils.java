package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

import com.intellij.util.ui.UIUtil;
import java.awt.Color;

public class ThemeUtils {

  public static Color getPanelBackgroundColor() {
    var panelBg = UIUtil.getPanelBackground();
    return UIUtil.isUnderDarcula() ? toDarker(panelBg) : panelBg.brighter();
  }

  private static Color toDarker(Color color) {
    var factor = 0.9;
    return new Color(
        Math.max((int) (color.getRed() * factor), 0),
        Math.max((int) (color.getGreen() * factor), 0),
        Math.max((int) (color.getBlue() * factor), 0),
        color.getAlpha());
  }

  public static String getRGB(Color color) {
    return format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue());
  }
}
