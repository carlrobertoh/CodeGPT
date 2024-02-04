package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
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

public class AzureServiceForm extends RemoteServiceForm<AzureCredentials> {

  private JBRadioButton useAzureApiKeyAuthenticationRadioButton;
  private JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
  private JBPasswordField azureActiveDirectoryTokenField;
  private JPanel azureActiveDirectoryTokenFieldPanel;
  private JBTextField azureResourceNameField;
  private JBTextField azureDeploymentIdField;
  private JBTextField azureApiVersionField;

  private JPanel apiKeyFieldPanel;

  public AzureServiceForm() {
    super(AzureSettings.getInstance().getState(), ServiceType.AZURE);
  }

  @Override
  protected List<PanelBuilder> authenticationComponents() {
    var azureSettings = AzureSettings.getInstance().getState();
    AzureCredentials credentials = azureSettings.getCredentials();
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
    return List.of(UIUtil.createSelectLayoutPanelBuilder(useAzureApiKeyAuthenticationRadioButton,
        apiKeyFieldPanel, useAzureActiveDirectoryAuthenticationRadioButton,
        azureActiveDirectoryTokenFieldPanel, azureSettings.isUseAzureApiKeyAuthentication()));
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    var azureSettings = AzureSettings.getInstance().getState();
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

  public AzureSettingsState getSettings() {
    return new AzureSettingsState(getBaseHost(), getPath(),
        new AzureCredentials(getApiKey(), getAzureActiveDirectoryToken()), getAzureResourceName(),
        getAzureDeploymentId(), getAzureApiVersion(), isAzureApiKeyAuthenticationSelected(),
        isAzureActiveDirectoryAuthenticationSelected());
  }

  public void setSettings(AzureSettingsState settings) {
    super.setSettings(settings);
    setAzureActiveDirectoryAuthenticationSelected(settings.isUseAzureActiveDirectoryAuthentication());
    setAzureActiveDirectoryToken(settings.getCredentials().getActiveDirectoryToken());
    setAzureApiKeyAuthenticationSelected(settings.isUseAzureApiKeyAuthentication());
    setAzureApiVersion(settings.getApiVersion());
    setAzureResourceName(settings.getResourceName());
    setAzureDeploymentId(settings.getDeploymentId());
  }

}
