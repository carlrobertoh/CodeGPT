package ee.carlrobert.codegpt.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;

public class IconActionButton extends ActionButton {

  public IconActionButton(AnAction action) {
    super(action,
        getPresentation(action),
        ActionPlaces.TOOLWINDOW_CONTENT,
        ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);
  }

  private static Presentation getPresentation(AnAction action) {
    var actionPresentation = action.getTemplatePresentation();
    var presentation = new Presentation(actionPresentation.getText());
    presentation.setIcon(actionPresentation.getIcon());
    return presentation;
  }
}
