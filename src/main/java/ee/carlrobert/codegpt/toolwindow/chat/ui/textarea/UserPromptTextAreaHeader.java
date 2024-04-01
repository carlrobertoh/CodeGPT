package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class UserPromptTextAreaHeader extends JPanel {

  public UserPromptTextAreaHeader(
      ServiceType selectedService,
      TotalTokensPanel totalTokensPanel,
      Runnable onAddNewTab) {
    super(new BorderLayout());
    setOpaque(false);
    setBorder(JBUI.Borders.emptyBottom(8));
    switch (selectedService) {
      case OPENAI:
      case AZURE:
        add(totalTokensPanel, BorderLayout.LINE_START);
        break;
      case YOU:
        break;
      default:
    }
    add(new ModelComboBoxAction(onAddNewTab, selectedService)
        .createCustomComponent(ActionPlaces.UNKNOWN), BorderLayout.LINE_END);
  }
}
