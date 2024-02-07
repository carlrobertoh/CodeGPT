package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.createApiKeyPanel;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.managers.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.util.List;
import javax.swing.JPanel;

/**
 * Form containing all forms to configure using
 * {@link ee.carlrobert.codegpt.settings.service.ServiceType#AZURE}.
 */

public class AzureServiceForm extends RemoteServiceForm {

  private JBRadioButton useAzureApiKeyAuthenticationRadioButton;
  private JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
  private JBPasswordField azureActiveDirectoryTokenField;
  private JPanel azureActiveDirectoryTokenFieldPanel;
  private JBTextField azureResourceNameField;
  private JBTextField azureDeploymentIdField;
  private JBTextField azureApiVersionField;
  private final JBPasswordField apiKeyField;

  public AzureServiceForm() {
    super(AzureSettings.getInstance().getState(), ServiceType.AZURE);
    this.apiKeyField = new JBPasswordField();
  }

  @Override
  protected List<PanelBuilder> authenticationPanels() {
    AzureCredentials credentials = AzureCredentialsManager.getInstance().getCredentials();
    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(credentials.getActiveDirectoryToken());
    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.azure.bearerToken.label"))
        .resizeX(false)
        .createPanel();
    var azureSettings = AzureSettings.getInstance().getState();
    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useApiKeyAuth.label"),
        azureSettings.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useActiveDirectoryAuth.label"),
        azureSettings.isUseAzureActiveDirectoryAuthentication());
    JPanel apiKeyFieldPanel = createApiKeyPanel(credentials.getApiKey(), apiKeyField).createPanel();

    List<PanelBuilder> panels = super.authenticationPanels();
    panels.add(UIUtil.createSelectLayoutPanelBuilder(useAzureApiKeyAuthenticationRadioButton,
        apiKeyFieldPanel, useAzureActiveDirectoryAuthenticationRadioButton,
        azureActiveDirectoryTokenFieldPanel, azureSettings.isUseAzureApiKeyAuthentication()));
    return panels;
  }

  @Override
  protected List<PanelBuilder> requestConfigurationPanels() {
    var azureSettings = AzureSettings.getInstance().getState();
    azureResourceNameField = new JBTextField(azureSettings.getResourceName(), 35);
    azureDeploymentIdField = new JBTextField(azureSettings.getDeploymentId(), 35);
    azureApiVersionField = new JBTextField(azureSettings.getApiVersion(), 35);
    List<PanelBuilder> panels = Lists.newArrayList(UI.PanelFactory.panel(azureResourceNameField)
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
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.apiVersion.comment"))
            .resizeX(false));
    panels.addAll(super.requestConfigurationPanels());
    return panels;
  }

  public AzureSettingsState getSettings() {
    return new AzureSettingsState(getBaseHost(), getPath(), azureResourceNameField.getText(),
        azureDeploymentIdField.getText(), azureApiVersionField.getText(),
        useAzureApiKeyAuthenticationRadioButton.isSelected(),
        useAzureActiveDirectoryAuthenticationRadioButton.isSelected());
  }

  public void setSettings(AzureSettingsState settings) {
    super.setSettings(settings);
    useAzureActiveDirectoryAuthenticationRadioButton.setSelected(
        settings.isUseAzureActiveDirectoryAuthentication());
    useAzureApiKeyAuthenticationRadioButton.setSelected(settings.isUseAzureApiKeyAuthentication());
    azureApiVersionField.setText(settings.getApiVersion());
    azureResourceNameField.setText(settings.getResourceName());
    azureDeploymentIdField.setText(settings.getDeploymentId());
  }

  public AzureCredentials getCredentials() {
    return new AzureCredentials(new String(apiKeyField.getPassword()),
        new String(azureActiveDirectoryTokenField.getPassword()));
  }

  public void setCredentials(AzureCredentials credentials) {
    apiKeyField.setText(credentials.getApiKey());
    azureActiveDirectoryTokenField.setText(credentials.getActiveDirectoryToken());
  }

}
