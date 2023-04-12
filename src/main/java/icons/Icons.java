package icons;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.util.FileUtils;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

  public static final Icon DefaultIcon = IconLoader.getIcon("/icons/codegpt-icon.svg", Icons.class);
  public static final Icon ToolWindowIcon = IconLoader.getIcon("/icons/toolwindow-icon.svg", Icons.class);
  public static final ImageIcon DefaultImageIcon = getImageIcon("/icons/chatgpt-icon.png");
  public static final ImageIcon SendImageIcon = getImageIcon("/icons/send-icon.png");
  public static final ImageIcon SunImageIcon = getImageIcon("/icons/sun-icon.png");

  public static String getHtmlSvgIcon(String iconName) {
    return FileUtils.getResource(String.format(UIUtil.isUnderDarcula() ? "/html/icons/%s_dark.svg" : "/html/icons/%s.svg", iconName));
  }

  private static ImageIcon getImageIcon(String path) {
    return new ImageIcon(Objects.requireNonNull(Icons.class.getResource(path)));
  }
}
