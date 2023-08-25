package ee.carlrobert.codegpt.settings;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

public class ServiceSelectionForm {

  private final JBRadioButton useOpenAIServiceRadioButton;
  private final JBRadioButton useAzureServiceRadioButton;

  private final JBPasswordField openAIApiKey;
  private final JBTextField openAIBaseHostField;
  private final JBTextField openAIOrganizationField;
  private final JPanel openAIServiceSectionPanel;

  private final JBRadioButton useAzureApiKeyAuthenticationRadioButton;
  private final JBPasswordField azureApiKeyField;
  private final JPanel azureApiKeyFieldPanel;
  private final JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
  private final JBPasswordField azureActiveDirectoryTokenField;
  private final JPanel azureActiveDirectoryTokenFieldPanel;
  private final JBTextField azureBaseHostField;
  private final JBTextField azureResourceNameField;
  private final JBTextField azureDeploymentIdField;
  private final JBTextField azureApiVersionField;
  private final JPanel azureServiceSectionPanel;

  public ServiceSelectionForm(SettingsState settings) {
    openAIApiKey = new JBPasswordField();
    openAIApiKey.setColumns(30);
    openAIApiKey.setText(OpenAICredentialsManager.getInstance().getApiKey());

    azureApiKeyField = new JBPasswordField();
    azureApiKeyField.setColumns(30);
    azureApiKeyField.setText(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey());

    azureApiKeyFieldPanel = UI.PanelFactory.panel(azureApiKeyField)
        .withLabel("API key:")
        .resizeX(false)
        .createPanel();

    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken());

    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel("Bearer token:")
        .resizeX(false)
        .createPanel();

    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        "Use API Key authentication",
        settings.useAzureApiKeyAuthentication);
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        "Use Active Directory authentication",
        settings.useAzureActiveDirectoryAuthentication);

    useOpenAIServiceRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.service.useOpenAIServiceRadioButtonLabel"), settings.useOpenAIService);
    useAzureServiceRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.service.useAzureServiceRadioButtonLabel"), settings.useAzureService);

    openAIBaseHostField = new JBTextField(settings.openAIBaseHost, 30);
    openAIOrganizationField = new JBTextField(settings.openAIOrganization, 30);

    azureBaseHostField = new JBTextField(settings.azureBaseHost, 30);
    azureResourceNameField = new JBTextField(settings.azureResourceName, 30);
    azureDeploymentIdField = new JBTextField(settings.azureDeploymentId, 30);
    azureApiVersionField = new JBTextField(settings.azureApiVersion, 30);

    openAIServiceSectionPanel = createOpenAIServiceSectionPanel();
    azureServiceSectionPanel = createAzureServiceSectionPanel();

    registerPanelsVisibility(settings);
    registerRadioButtons();
  }

  public JPanel getForm() {
    var form = FormBuilder.createFormBuilder()
        .addComponent(useOpenAIServiceRadioButton)
        .addComponent(openAIServiceSectionPanel)
        .addComponent(useAzureServiceRadioButton)
        .addComponent(azureServiceSectionPanel)
        .getPanel();
    form.setBorder(JBUI.Borders.emptyLeft(16));
    return form;
  }

  public void setOpenAIServiceSelected(boolean selected) {
    useOpenAIServiceRadioButton.setSelected(selected);
  }

  public boolean isOpenAIServiceSelected() {
    return useOpenAIServiceRadioButton.isSelected();
  }

  public void setAzureServiceSelected(boolean selected) {
    useAzureServiceRadioButton.setSelected(selected);
  }

  public boolean isAzureServiceSelected() {
    return useAzureServiceRadioButton.isSelected();
  }

  public void setOpenAIApiKey(String apiKey) {
    openAIApiKey.setText(apiKey);
  }

  public String getOpenAIApiKey() {
    return new String(openAIApiKey.getPassword());
  }

  public void setOpenAIBaseHost(String baseHost) {
    openAIBaseHostField.setText(baseHost);
  }

  public String getOpenAIBaseHost() {
    return openAIBaseHostField.getText();
  }

  public void setOpenAIOrganization(String organization) {
    openAIOrganizationField.setText(organization);
  }

  public String getOpenAIOrganization() {
    return openAIOrganizationField.getText();
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

  public void setAzureApiKey(String apiKey) {
    azureApiKeyField.setText(apiKey);
  }

  public String getAzureOpenAIApiKey() {
    return new String(azureApiKeyField.getPassword());
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

  public void setAzureBaseHost(String baseHost) {
    azureBaseHostField.setText(baseHost);
  }

  public String getAzureBaseHost() {
    return azureBaseHostField.getText();
  }

  private JPanel createOpenAIServiceSectionPanel() {
    var panel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(openAIApiKey)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.integration.apiKeyField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.section.integration.apiKeyField.comment"))
            .withCommentHyperlinkListener(SwingUtils::handleHyperlinkClicked))
        .add(UI.PanelFactory.panel(openAIOrganizationField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.service.openai.organizationField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.section.service.openai.organizationField.comment")))
        .add(UI.PanelFactory.panel(openAIBaseHostField)
            .withLabel("Base host:")
            .resizeX(false))
        .createPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  private JPanel createAzureServiceSectionPanel() {
    var gridPanel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(azureResourceNameField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.service.azure.resourceNameField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.section.service.azure.resourceNameField.comment")))
        .add(UI.PanelFactory.panel(azureDeploymentIdField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.service.azure.deploymentIdField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.section.service.azure.deploymentIdField.comment")))
        .add(UI.PanelFactory.panel(azureApiVersionField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.section.service.azure.apiVersionField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.section.service.azure.apiVersionField.comment")))
        .add(UI.PanelFactory.panel(azureBaseHostField)
            .withLabel("Base host:")
            .resizeX(false))
        .createPanel();
    gridPanel.setBorder(JBUI.Borders.emptyLeft(16));

    azureApiKeyFieldPanel.setBorder(JBUI.Borders.emptyLeft(16));
    azureActiveDirectoryTokenFieldPanel.setBorder(JBUI.Borders.emptyLeft(16));

    var authenticationPanel = FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(useAzureApiKeyAuthenticationRadioButton).resizeX(false).createPanel())
        .addComponent(azureApiKeyFieldPanel)
        .addComponent(UI.PanelFactory.panel(useAzureActiveDirectoryAuthenticationRadioButton).resizeX(false).createPanel())
        .addComponent(azureActiveDirectoryTokenFieldPanel)
        .getPanel();
    authenticationPanel.setBorder(JBUI.Borders.emptyLeft(16));

    var form = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Authentication"))
        .addComponent(authenticationPanel)
        .addComponent(new TitledSeparator("Request Configuration"))
        .addComponent(gridPanel)
        .getPanel();
    form.setBorder(JBUI.Borders.emptyLeft(16));
    return form;
  }

  private void registerPanelsVisibility(SettingsState settings) {
    openAIServiceSectionPanel.setVisible(settings.useOpenAIService);
    azureServiceSectionPanel.setVisible(settings.useAzureService);
    azureApiKeyFieldPanel.setVisible(settings.useAzureApiKeyAuthentication);
    azureActiveDirectoryTokenFieldPanel.setVisible(settings.useAzureActiveDirectoryAuthentication);
  }

  private void registerRadioButtons() {
    registerRadioButtons(
        Map.entry(useOpenAIServiceRadioButton, openAIServiceSectionPanel),
        Map.entry(useAzureServiceRadioButton, azureServiceSectionPanel));
    registerRadioButtons(
        Map.entry(useAzureApiKeyAuthenticationRadioButton, azureApiKeyFieldPanel),
        Map.entry(useAzureActiveDirectoryAuthenticationRadioButton, azureActiveDirectoryTokenFieldPanel));
  }

  private void registerRadioButtons(Map.Entry<JBRadioButton, JPanel> firstEntry, Map.Entry<JBRadioButton, JPanel> secondEntry) {
    var buttonGroup = new ButtonGroup();
    buttonGroup.add(firstEntry.getKey());
    buttonGroup.add(secondEntry.getKey());

    firstEntry.getKey().addActionListener(e -> {
      firstEntry.getValue().setVisible(true);
      secondEntry.getValue().setVisible(false);
    });
    secondEntry.getKey().addActionListener(e -> {
      firstEntry.getValue().setVisible(false);
      secondEntry.getValue().setVisible(true);
    });
  }
}
