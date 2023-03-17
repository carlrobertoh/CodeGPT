package ee.carlrobert.codegpt.ide.toolwindow.chat.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class UsageToolbarLabelAction extends ToolbarLabelAction {

  @Override
  public @NotNull JComponent createCustomComponent(
      @NotNull Presentation presentation,
      @NotNull String place) {
    var client = new ClientFactory().getClient();
    var creditGrants = client.getCreditGrants();
    JComponent component = super.createCustomComponent(presentation, place);
    component.setBorder(JBUI.Borders.empty(0, 2));
    if (creditGrants != null) {
      presentation.setText(String.format("Credit used: %.2f/%.2f USD", creditGrants.getTotalUsed(), creditGrants.getTotalGranted()));
    }
    return component;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    event.getPresentation().setVisible(!SettingsState.getInstance().apiKey.isEmpty());
  }
}
