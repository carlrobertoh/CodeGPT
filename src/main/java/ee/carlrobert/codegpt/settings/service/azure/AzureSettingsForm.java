package ee.carlrobert.codegpt.settings.service.azure;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

public class AzureSettingsForm {

  private final JBRadioButton useAzureApiKeyAuthenticationRadioButton;
  private final JBPasswordField azureApiKeyField;
  private final JPanel azureApiKeyFieldPanel;
  private final JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
  private final JBPasswordField azureActiveDirectoryTokenField;
  private final JPanel azureActiveDirectoryTokenFieldPanel;
  private final JBTextField azureBaseHostField;
  private final JBTextField azurePathField;
  private final JBTextField azureResourceNameField;
  private final JBTextField azureDeploymentIdField;
  private final JBTextField azureApiVersionField;

  public AzureSettingsForm(AzureSettingsState settings) {
    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useApiKeyAuth.label"),
        settings.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useActiveDirectoryAuth.label"),
        settings.isUseAzureActiveDirectoryAuthentication());
    azureApiKeyField = new JBPasswordField();
    azureApiKeyField.setColumns(30);
    azureApiKeyField.setText(AzureCredentialsManager.getInstance().getApiKey());
    azureApiKeyFieldPanel = UI.PanelFactory.panel(azureApiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .createPanel();
    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(
        AzureCredentialsManager.getInstance().getActiveDirectoryToken());
    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.azure.bearerToken.label"))
        .resizeX(false)
        .createPanel();
    azureBaseHostField = new JBTextField(settings.getBaseHost(), 35);
    azurePathField = new JBTextField(settings.getPath(), 35);
    azureResourceNameField = new JBTextField(settings.getResourceName(), 35);
    azureDeploymentIdField = new JBTextField(settings.getDeploymentId(), 35);
    azureApiVersionField = new JBTextField(settings.getApiVersion(), 35);

    registerPanelsVisibility(settings);
    registerRadioButtons();
  }

  public JPanel getForm() {
    var authPanel = withEmptyLeftBorder(FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory
            .panel(useAzureApiKeyAuthenticationRadioButton)
            .resizeX(false)
            .createPanel())
        .addComponent(withEmptyLeftBorder(azureApiKeyFieldPanel))
        .addComponent(UI.PanelFactory
            .panel(useAzureActiveDirectoryAuthenticationRadioButton)
            .resizeX(false)
            .createPanel())
        .addComponent(withEmptyLeftBorder(azureActiveDirectoryTokenFieldPanel))
        .getPanel());

    var configPanel = withEmptyLeftBorder(UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(azureResourceNameField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.resourceName.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.resourceName.comment")))
        .add(UI.PanelFactory.panel(azureDeploymentIdField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.deploymentId.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.deploymentId.comment")))
        .add(UI.PanelFactory.panel(azureApiVersionField)
            .withLabel(CodeGPTBundle.get(
                "shared.apiVersion"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.service.azure.apiVersion.comment")))
        .add(UI.PanelFactory.panel(azureBaseHostField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(azurePathField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.path.label"))
            .resizeX(false))
        .createPanel());

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(authPanel)
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
        .addComponent(configPanel)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public AzureSettingsState getCurrentState() {
    var state = new AzureSettingsState();
    state.setUseAzureActiveDirectoryAuthentication(
        useAzureActiveDirectoryAuthenticationRadioButton.isSelected());
    state.setUseAzureApiKeyAuthentication(useAzureApiKeyAuthenticationRadioButton.isSelected());
    state.setResourceName(azureResourceNameField.getText());
    state.setDeploymentId(azureDeploymentIdField.getText());
    state.setApiVersion(azureApiVersionField.getText());
    state.setBaseHost(azureBaseHostField.getText());
    state.setPath(azurePathField.getText());
    return state;
  }

  public void resetForm() {
    var state = AzureSettings.getCurrentState();
    azureApiKeyField.setText(AzureCredentialsManager.getInstance().getApiKey());
    azureActiveDirectoryTokenField.setText(
        AzureCredentialsManager.getInstance().getActiveDirectoryToken());
    useAzureApiKeyAuthenticationRadioButton.setSelected(state.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton.setSelected(
        state.isUseAzureActiveDirectoryAuthentication());
    azureResourceNameField.setText(state.getResourceName());
    azureDeploymentIdField.setText(state.getDeploymentId());
    azureApiVersionField.setText(state.getApiVersion());
    azureBaseHostField.setText(state.getBaseHost());
    azurePathField.setText(state.getPath());
  }

  public @Nullable String getActiveDirectoryToken() {
    var activeDirToken = new String(azureActiveDirectoryTokenField.getPassword());
    if (activeDirToken.isEmpty()) {
      return null;
    }
    return activeDirToken;
  }

  public @Nullable String getApiKey() {
    var apiKey = new String(azureApiKeyField.getPassword());
    return apiKey.isEmpty() ? null : apiKey;
  }

  private void registerPanelsVisibility(AzureSettingsState azureSettings) {
    azureApiKeyFieldPanel.setVisible(azureSettings.isUseAzureApiKeyAuthentication());
    azureActiveDirectoryTokenFieldPanel.setVisible(
        azureSettings.isUseAzureActiveDirectoryAuthentication());
  }

  private void registerRadioButtons() {
    registerRadioButtons(List.of(
        Map.entry(useAzureApiKeyAuthenticationRadioButton, azureApiKeyFieldPanel),
        Map.entry(useAzureActiveDirectoryAuthenticationRadioButton,
            azureActiveDirectoryTokenFieldPanel)));
  }

  // TODO: Move
  private void registerRadioButtons(List<Map.Entry<JBRadioButton, JPanel>> entries) {
    var buttonGroup = new ButtonGroup();
    entries.forEach(entry -> buttonGroup.add(entry.getKey()));
    entries.forEach(entry -> entry.getKey().addActionListener((e) -> {
      for (Map.Entry<JBRadioButton, JPanel> innerEntry : entries) {
        innerEntry.getValue().setVisible(innerEntry.equals(entry));
      }
    }));
  }
}
