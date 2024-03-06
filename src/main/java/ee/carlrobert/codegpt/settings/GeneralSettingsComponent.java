package ee.carlrobert.codegpt.settings;

import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class GeneralSettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField displayNameField;
  private final ComboBox<ServiceType> serviceComboBox;
  private final ServiceSelectionForm serviceSelectionForm;

  public GeneralSettingsComponent(Disposable parentDisposable, GeneralSettings settings) {
    displayNameField = new JBTextField(settings.getState().getDisplayName(), 20);
    serviceSelectionForm = new ServiceSelectionForm(parentDisposable);
    var cardLayout = new DynamicCardLayout();
    var cards = new JPanel(cardLayout);
    cards.add(serviceSelectionForm.getOpenAISettingsForm().getForm(), OPENAI.getCode());
    cards.add(
        serviceSelectionForm.getCustomConfigurationSettingsForm().getForm(),
        CUSTOM_OPENAI.getCode());
    cards.add(serviceSelectionForm.getAnthropicSettingsForm().getForm(), ANTHROPIC.getCode());
    cards.add(serviceSelectionForm.getAzureSettingsForm().getForm(), AZURE.getCode());
    cards.add(serviceSelectionForm.getYouSettingsForm(), YOU.getCode());
    cards.add(serviceSelectionForm.getLlamaSettingsForm(), LLAMA_CPP.getCode());
    var serviceComboBoxModel = new DefaultComboBoxModel<ServiceType>();
    serviceComboBoxModel.addAll(Arrays.stream(ServiceType.values())
        .collect(toList()));
    serviceComboBox = new ComboBox<>(serviceComboBoxModel);
    serviceComboBox.setSelectedItem(OPENAI);
    serviceComboBox.setPreferredSize(displayNameField.getPreferredSize());
    serviceComboBox.addItemListener(e ->
        cardLayout.show(cards, ((ServiceType) e.getItem()).getCode()));
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

  static class DynamicCardLayout extends CardLayout {

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      Component current = findVisibleComponent(parent);
      if (current != null) {
        Insets insets = parent.getInsets();
        Dimension preferredSize = current.getPreferredSize();
        preferredSize.width += insets.left + insets.right;
        preferredSize.height += insets.top + insets.bottom;
        return preferredSize;
      }
      return super.preferredLayoutSize(parent);
    }

    private Component findVisibleComponent(Container parent) {
      for (Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          return comp;
        }
      }
      return null;
    }
  }
}
