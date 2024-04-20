package ee.carlrobert.codegpt.settings;

import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettingsForm;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm;
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
  private final OpenAISettingsForm openAISettingsForm;
  private final CustomServiceForm customConfigurationSettingsForm;
  private final AnthropicSettingsForm anthropicSettingsForm;
  private final AzureSettingsForm azureSettingsForm;
  private final YouSettingsForm youSettingsForm;
  private final LlamaSettingsForm llamaSettingsForm;

  public GeneralSettingsComponent(Disposable parentDisposable, GeneralSettings settings) {
    displayNameField = new JBTextField(settings.getState().getDisplayName(), 20);
    openAISettingsForm = new OpenAISettingsForm(OpenAISettings.getCurrentState());
    customConfigurationSettingsForm = new CustomServiceForm();
    anthropicSettingsForm = new AnthropicSettingsForm(AnthropicSettings.getCurrentState());
    azureSettingsForm = new AzureSettingsForm(AzureSettings.getCurrentState());
    youSettingsForm = new YouSettingsForm(YouSettings.getCurrentState(), parentDisposable);
    llamaSettingsForm = new LlamaSettingsForm(LlamaSettings.getCurrentState());

    var cardLayout = new DynamicCardLayout();
    var cards = new JPanel(cardLayout);
    cards.add(openAISettingsForm.getForm(), OPENAI.getCode());
    cards.add(customConfigurationSettingsForm.getForm(), CUSTOM_OPENAI.getCode());
    cards.add(anthropicSettingsForm.getForm(), ANTHROPIC.getCode());
    cards.add(azureSettingsForm.getForm(), AZURE.getCode());
    cards.add(youSettingsForm, YOU.getCode());
    cards.add(llamaSettingsForm, LLAMA_CPP.getCode());
    var serviceComboBoxModel = new DefaultComboBoxModel<ServiceType>();
    serviceComboBoxModel.addAll(Arrays.stream(ServiceType.values()).toList());
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

  public OpenAISettingsForm getOpenAISettingsForm() {
    return openAISettingsForm;
  }

  public CustomServiceForm getCustomConfigurationSettingsForm() {
    return customConfigurationSettingsForm;
  }

  public AnthropicSettingsForm getAnthropicSettingsForm() {
    return anthropicSettingsForm;
  }

  public AzureSettingsForm getAzureSettingsForm() {
    return azureSettingsForm;
  }

  public LlamaSettingsForm getLlamaSettingsForm() {
    return llamaSettingsForm;
  }

  public YouSettingsForm getYouSettingsForm() {
    return youSettingsForm;
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

  public String getDisplayName() {
    return displayNameField.getText();
  }

  public void setDisplayName(String displayName) {
    displayNameField.setText(displayName);
  }

  public void resetForms() {
    openAISettingsForm.resetForm();
    customConfigurationSettingsForm.resetForm();
    anthropicSettingsForm.resetForm();
    azureSettingsForm.resetForm();
    youSettingsForm.resetForm();
    llamaSettingsForm.resetForm();
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
