package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Color;
import javax.swing.UIManager;

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

  public static String getDisabledTextColorRGB() {
    return getRGB(UIUtil.getInactiveTextColor());
  }

  public static String getPanelBackgroundColorRGB() {
    var panelBg = UIUtil.getPanelBackground();
    return getRGB(UIUtil.isUnderDarcula() ? toDarker(panelBg) : panelBg.brighter());
  }

  public static String getButtonBackgroundColorRGB() {
    return getRGB(UIManager.getColor("Button.background"));
  }

  public static String getDisabledButtonBackgroundColorRGB() {
    return getRGB(UIUtil.getInactiveTextFieldBackgroundColor());
  }

  public static int getFontSize() {
    return JBFont.regular().getSize();
  }

  public static String getScrollBarForegroundColorRGB() {
    return "rgb(187, 187, 187, 0.8)"; // TODO: Get theme's scrollbar color
  }

  public static int getScrollBarRadius() {
    return SystemInfo.isMac ? 10 : 0;
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
