package ee.carlrobert.codegpt.ide.toolwindow.chat.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction;
import com.intellij.util.ui.JBUI;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class TokenToolbarLabelAction extends ToolbarLabelAction {

  @Override
  public @NotNull JComponent createCustomComponent(
      @NotNull Presentation presentation,
      @NotNull String place) {
    JComponent component = super.createCustomComponent(presentation, place);
    component.setBorder(JBUI.Borders.empty(0, 2));
    return component;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    super.update(e);
    Presentation presentation = e.getPresentation();
    presentation.setText("Tokens: 0/0");
    presentation.setVisible(true);
  }
}
