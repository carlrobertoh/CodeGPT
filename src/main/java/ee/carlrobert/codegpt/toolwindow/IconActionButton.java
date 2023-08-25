package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import javax.swing.Icon;

public class IconActionButton extends ActionButton {

  public IconActionButton(String tooltipText, Icon icon, AnAction onAction) {
    super(onAction,
        getPresentation(tooltipText, icon),
        ActionPlaces.TOOLWINDOW_CONTENT,
        ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);
  }

  private static Presentation getPresentation(String tooltipText, Icon icon) {
    var presentation = new Presentation(tooltipText);
    presentation.setIcon(icon);
    return presentation;
  }
}
