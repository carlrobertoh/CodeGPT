package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField displayNameField;
  private final ServiceSelectionForm serviceSelectionForm;
  private final YouServiceSelectionForm youServiceSelectionForm;

  public SettingsComponent(Disposable parentDisposable, SettingsState settings) {
    serviceSelectionForm = new ServiceSelectionForm(parentDisposable, settings);
    displayNameField = new JBTextField(settings.getDisplayName(), 20);
    youServiceSelectionForm = new YouServiceSelectionForm(parentDisposable);
    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(displayNameField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.integration.displayNameFieldLabel"))
            .resizeX(false)
            .createPanel())
        .addComponent(new TitledSeparator(CodeGPTBundle.get("settingsConfigurable.section.service.title")))
        .addComponent(serviceSelectionForm.getForm())
        .addVerticalGap(8)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return displayNameField;
  }

  public String getEmail() {
    return youServiceSelectionForm.getEmail();
  }

  public void setEmail(String email) {
    youServiceSelectionForm.setEmail(email);
  }

  public String getPassword() {
    return youServiceSelectionForm.getPassword();
  }

  public ServiceSelectionForm getServiceSelectionForm() {
    return serviceSelectionForm;
  }

  public String getDisplayName() {
    return displayNameField.getText();
  }

  public void setDisplayName(String displayName) {
    displayNameField.setText(displayName);
  }
}
