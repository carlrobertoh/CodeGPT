package icons;

import com.intellij.openapi.util.IconLoader;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

  public static final Icon DefaultIcon = IconLoader.getIcon("/icons/chatgpt-icon.svg", Icons.class);
  public static final Icon SquareIcon = IconLoader.getIcon("/icons/square-icon.svg", Icons.class);
  public static final Icon RefreshIcon = IconLoader.getIcon("/icons/refresh-icon.svg", Icons.class);
  public static final Icon CommandIcon = IconLoader.getIcon("/icons/command-icon.svg", Icons.class);
  public static final Icon QuestionMarkIcon = IconLoader.getIcon("/icons/question-mark-icon.svg", Icons.class);
  public static final ImageIcon DefaultImageIcon = getImageIcon("/icons/chatgpt-icon.png");
  public static final ImageIcon SendImageIcon = getImageIcon("/icons/send-icon.png");
  public static final ImageIcon SunImageIcon = getImageIcon("/icons/sun-icon.png");
  public static final ImageIcon UserImageIcon = getImageIcon("/icons/user-icon.png");
  public static final ImageIcon CopyImageIcon = getImageIcon("/icons/copy-icon.png");
  public static final ImageIcon DoubleTickImageIcon = getImageIcon("/icons/double-tick-icon.png");

  private static ImageIcon getImageIcon(String path) {
    return new ImageIcon(Objects.requireNonNull(Icons.class.getResource(path)));
  }
}
