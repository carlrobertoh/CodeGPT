package ee.carlrobert.codegpt;

import com.intellij.openapi.util.IconLoader;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class Icons {

  public static final Icon DefaultIcon = IconLoader.getIcon("/icons/codegpt.svg", Icons.class);
  public static final Icon DefaultSmallIcon = IconLoader.getIcon("/icons/codegpt-small.svg", Icons.class);
  public static final Icon SendIcon = IconLoader.getIcon("/icons/send.svg", Icons.class);
  public static final ImageIcon DefaultImageIcon = getImageIcon("/icons/chatgpt.png");

  private static ImageIcon getImageIcon(String path) {
    return new ImageIcon(Objects.requireNonNull(Icons.class.getResource(path)));
  }
}
