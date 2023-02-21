package icons;

import com.intellij.openapi.util.IconLoader;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

  public static final Icon DefaultIcon = IconLoader.getIcon("/icons/chatgpt-icon.svg", Icons.class);
  public static final ImageIcon DefaultImageIcon = getImageIcon("/icons/chatgpt-icon.png");
  public static final ImageIcon SendImageIcon = getImageIcon("/icons/send-icon.png");
  public static final ImageIcon SunImageIcon = getImageIcon("/icons/sun-icon.png");
  public static final ImageIcon UserImageIcon = getImageIcon("/icons/user-icon.png");

  private static ImageIcon getImageIcon(String path) {
    return new ImageIcon(Objects.requireNonNull(Icons.class.getResource(path)));
  }
}
