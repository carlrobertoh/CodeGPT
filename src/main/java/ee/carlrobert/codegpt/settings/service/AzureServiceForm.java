package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.openai.OpenAiModelSelector;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceWithModelForm;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Form containing all forms to configure using
 * {@link ee.carlrobert.codegpt.settings.service.ServiceType#AZURE}
 */

public class AzureServiceForm extends
    RemoteServiceWithModelForm<AzureCredentialsManager, OpenAIChatCompletionModel> {

  private JBRadioButton useAzureApiKeyAuthenticationRadioButton;
  private JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
  private JBPasswordField azureActiveDirectoryTokenField;
  private JPanel azureActiveDirectoryTokenFieldPanel;
  private JBTextField azureResourceNameField;
  private JBTextField azureDeploymentIdField;
  private JBTextField azureApiVersionField;

  private JPanel apiKeyFieldPanel;

  public AzureServiceForm() {
    super(AzureSettingsState.getInstance(), ServiceType.AZURE, new OpenAiModelSelector());
  }

  @Override
  protected List<PanelBuilder> authenticationComponents() {
    var azureSettings = AzureSettingsState.getInstance();
    AzureCredentialsManager credentials = azureSettings.getCredentialsManager();
    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(credentials.getActiveDirectoryToken());
    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.azure.bearerToken.label"))
        .resizeX(false)
        .createPanel();
    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useApiKeyAuth.label"),
        azureSettings.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useActiveDirectoryAuth.label"),
        azureSettings.isUseAzureActiveDirectoryAuthentication());

    apiKeyFieldPanel = super.authenticationComponents().get(0).createPanel();
    registerPanelsVisibility(azureSettings);
    registerRadioButtons();
    return List.of(UI.PanelFactory
            .panel(useAzureApiKeyAuthenticationRadioButton)
            .resizeX(false),
        UI.PanelFactory
            .panel(apiKeyFieldPanel),
        UI.PanelFactory
            .panel(useAzureActiveDirectoryAuthenticationRadioButton)
            .resizeX(false),
        UI.PanelFactory
            .panel(azureActiveDirectoryTokenFieldPanel)
    );
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    var azureSettings = AzureSettingsState.getInstance();

    azureResourceNameField = new JBTextField(azureSettings.getResourceName(), 35);
    azureDeploymentIdField = new JBTextField(azureSettings.getDeploymentId(), 35);
    azureApiVersionField = new JBTextField(azureSettings.getApiVersion(), 35);
    List<PanelBuilder> panels = super.additionalServerConfigPanels();
    panels.addAll(List.of(UI.PanelFactory.panel(azureResourceNameField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.resourceName.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.resourceName.comment")),
        UI.PanelFactory.panel(azureDeploymentIdField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.deploymentId.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.deploymentId.comment")),
        UI.PanelFactory.panel(azureApiVersionField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.apiVersion.label"))
            .resizeX(false)));
    return panels;
  }

  private void registerPanelsVisibility(AzureSettingsState azureSettings) {
    apiKeyFieldPanel.setVisible(azureSettings.isUseAzureApiKeyAuthentication());
    azureActiveDirectoryTokenFieldPanel.setVisible(
        azureSettings.isUseAzureActiveDirectoryAuthentication());
  }

  private void registerRadioButtons() {
    registerRadioButtons(List.of(
        Map.entry(useAzureApiKeyAuthenticationRadioButton, apiKeyFieldPanel),
        Map.entry(useAzureActiveDirectoryAuthenticationRadioButton,
            azureActiveDirectoryTokenFieldPanel)));
  }

  private void registerRadioButtons(List<Map.Entry<JBRadioButton, JComponent>> entries) {
    var buttonGroup = new ButtonGroup();
    entries.forEach(entry -> buttonGroup.add(entry.getKey()));
    entries.forEach(entry -> entry.getKey().addActionListener((e) -> {
      for (Map.Entry<JBRadioButton, JComponent> innerEntry : entries) {
        innerEntry.getValue().setVisible(innerEntry.equals(entry));
      }
    }));
  }


  public void setAzureActiveDirectoryAuthenticationSelected(boolean selected) {
    useAzureActiveDirectoryAuthenticationRadioButton.setSelected(selected);
  }

  public boolean isAzureActiveDirectoryAuthenticationSelected() {
    return useAzureActiveDirectoryAuthenticationRadioButton.isSelected();
  }

  public void setAzureActiveDirectoryToken(String bearerToken) {
    azureActiveDirectoryTokenField.setText(bearerToken);
  }

  public String getAzureActiveDirectoryToken() {
    return new String(azureActiveDirectoryTokenField.getPassword());
  }

  public void setAzureApiKeyAuthenticationSelected(boolean selected) {
    useAzureApiKeyAuthenticationRadioButton.setSelected(selected);
  }

  public boolean isAzureApiKeyAuthenticationSelected() {
    return useAzureApiKeyAuthenticationRadioButton.isSelected();
  }

  public void setAzureResourceName(String resourceName) {
    azureResourceNameField.setText(resourceName);
  }

  public String getAzureResourceName() {
    return azureResourceNameField.getText();
  }

  public void setAzureDeploymentId(String deploymentId) {
    azureDeploymentIdField.setText(deploymentId);
  }

  public String getAzureDeploymentId() {
    return azureDeploymentIdField.getText();
  }

  public void setAzureApiVersion(String apiVersion) {
    azureApiVersionField.setText(apiVersion);
  }

  public String getAzureApiVersion() {
    return azureApiVersionField.getText();
  }


}
