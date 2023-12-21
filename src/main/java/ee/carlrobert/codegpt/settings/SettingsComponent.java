package ee.carlrobert.codegpt.settings;

import static java.util.stream.Collectors.toList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.awt.CardLayout;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField displayNameField;
  private final ComboBox<ServiceType> serviceComboBox;
  private final ServiceSelectionForm serviceSelectionForm;

  public SettingsComponent(Disposable parentDisposable, SettingsState settings) {
    displayNameField = new JBTextField(settings.getDisplayName(), 20);

    serviceSelectionForm = new ServiceSelectionForm(parentDisposable);
    var cardLayout = new CardLayout();
    var cards = new JPanel(cardLayout);
    cards.add(serviceSelectionForm.getOpenAIServiceSectionPanel(), ServiceType.OPENAI.getCode());
    cards.add(serviceSelectionForm.getAzureServiceSectionPanel(), ServiceType.AZURE.getCode());
    cards.add(serviceSelectionForm.getYouServiceSectionPanel(), ServiceType.YOU.getCode());
    cards.add(serviceSelectionForm.getLlamaServiceSectionPanel(), ServiceType.LLAMA_CPP.getCode());
    var serviceComboBoxModel = new DefaultComboBoxModel<ServiceType>();
    serviceComboBoxModel.addAll(Arrays.stream(ServiceType.values())
        .filter(it -> ServiceType.LLAMA_CPP != it || SystemInfoRt.isUnix)
        .collect(toList()));
    serviceComboBox = new ComboBox<>(serviceComboBoxModel);
    serviceComboBox.setSelectedItem(ServiceType.OPENAI);
    serviceComboBox.setPreferredSize(displayNameField.getPreferredSize());
    var serviceInputValidator = createInputValidator(parentDisposable, serviceComboBox);
    serviceInputValidator.revalidate();
    serviceComboBox.addItemListener(e -> {
      serviceInputValidator.revalidate();
      cardLayout.show(cards, ((ServiceType) e.getItem()).getCode());
    });

    mainPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.displayName.label"),
            displayNameField)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.label"),
            serviceComboBox)
        .addComponent(cards)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public ServiceType getSelectedService() {
    return serviceComboBox.getItem();
  }

  public void setSelectedService(ServiceType serviceType) {
    serviceComboBox.setSelectedItem(serviceType);
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return displayNameField;
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

  private ComponentValidator createInputValidator(
      Disposable parentDisposable,
      JComponent component) {
    var validator = new ComponentValidator(parentDisposable)
        .withValidator(() -> {
          if (component instanceof ComboBox) {
            var selectedItem = ((ComboBox<?>) component).getSelectedItem();
            if (selectedItem == ServiceType.OPENAI
                && OpenAISettingsState.getInstance().isOpenAIQuotaExceeded()) {
              return new ValidationInfo(
                  CodeGPTBundle.get("settings.openaiQuotaExceeded"),
                  component);
            }
          }

          return null;
        })
        .andStartOnFocusLost()
        .installOn(component);
    validator.enableValidation();
    return validator;
  }
}
