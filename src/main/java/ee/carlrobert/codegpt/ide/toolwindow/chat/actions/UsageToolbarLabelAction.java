package ee.carlrobert.codegpt.ide.toolwindow.chat.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.ide.account.AccountDetailsState;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class UsageToolbarLabelAction extends ToolbarLabelAction {

  @Override
  public @NotNull JComponent createCustomComponent(
      @NotNull Presentation presentation,
      @NotNull String place) {
    JComponent component = super.createCustomComponent(presentation, place);
    component.setBorder(JBUI.Borders.empty(0, 2));
    return component;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);

    var accountDetails = AccountDetailsState.getInstance();
    var isCreditUsageAvailable = accountDetails.totalAmountUsed != null && accountDetails.totalAmountGranted != null;
    var presentation = event.getPresentation();
    presentation.setVisible(isCreditUsageAvailable);
    if (isCreditUsageAvailable) {
      presentation.setText(String.format("Credit used: %.2f / %.2f USD",
          accountDetails.totalAmountUsed,
          accountDetails.totalAmountGranted));
    }
  }
}
