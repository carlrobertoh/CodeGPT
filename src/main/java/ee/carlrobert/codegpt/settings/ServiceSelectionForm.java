package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationNotifier;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServiceSelectionForm {

  private static final Logger LOG = Logger.getInstance(ServiceSelectionForm.class);

  private final Disposable parentDisposable;

  private final JBPasswordField openAIApiKeyField;
  private final JBTextField openAIBaseHostField;
  private final JBTextField openAIPathField;
  private final JBTextField openAIOrganizationField;
  private final JPanel openAIServiceSectionPanel;
  private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

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
  private final JPanel azureServiceSectionPanel;
  private final ComboBox<OpenAIChatCompletionModel> azureCompletionModelComboBox;

  private final JPanel youServiceSectionPanel;
  private final JBCheckBox displayWebSearchResultsCheckBox;

  private final LlamaServiceSelectionForm llamaServiceSectionPanel;

  public ServiceSelectionForm(Disposable parentDisposable, SettingsState settings) {
    this.parentDisposable = parentDisposable;
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    openAIApiKeyField = new JBPasswordField();
    openAIApiKeyField.setColumns(30);
    openAIApiKeyField.setText(OpenAICredentialsManager.getInstance().getApiKey());

    azureApiKeyField = new JBPasswordField();
    azureApiKeyField.setColumns(30);
    azureApiKeyField.setText(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey());
    azureApiKeyFieldPanel = UI.PanelFactory.panel(azureApiKeyField)
        .withLabel("API key:")
        .resizeX(false)
        .createPanel();
    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(
        AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken());
    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel("Bearer token:")
        .resizeX(false)
        .createPanel();
    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        "Use API Key authentication",
        azureSettings.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        "Use Active Directory authentication",
        azureSettings.isUseAzureActiveDirectoryAuthentication());

    openAIBaseHostField = new JBTextField(openAISettings.getBaseHost(), 30);
    openAIPathField = new JBTextField(openAISettings.getPath(), 30);
    openAIOrganizationField = new JBTextField(openAISettings.getOrganization(), 30);

    var selectedOpenAIModel = OpenAIChatCompletionModel.findByCode(openAISettings.getModel());

    openAICompletionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    openAICompletionModelComboBox.setSelectedItem(selectedOpenAIModel);

    azureBaseHostField = new JBTextField(azureSettings.getBaseHost(), 35);
    azurePathField = new JBTextField(azureSettings.getPath(), 35);
    azureResourceNameField = new JBTextField(azureSettings.getResourceName(), 35);
    azureDeploymentIdField = new JBTextField(azureSettings.getDeploymentId(), 35);
    azureApiVersionField = new JBTextField(azureSettings.getApiVersion(), 35);
    azureCompletionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    azureCompletionModelComboBox.setSelectedItem(selectedOpenAIModel);
    azureCompletionModelComboBox.getEditor()
        .getEditorComponent()
        .setMaximumSize(azureBaseHostField.getPreferredSize());

    displayWebSearchResultsCheckBox = new JBCheckBox(
        "Display web search results",
        YouSettingsState.getInstance().isDisplayWebSearchResults());
    displayWebSearchResultsCheckBox.setEnabled(YouUserManager.getInstance().isAuthenticated());

    openAIServiceSectionPanel = createOpenAIServiceSectionPanel();
    azureServiceSectionPanel = createAzureServiceSectionPanel();
    youServiceSectionPanel = createYouServiceSectionPanel();
    llamaServiceSectionPanel = new LlamaServiceSelectionForm();

    registerPanelsVisibility(azureSettings);
    registerRadioButtons();

    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(AuthenticationNotifier.AUTHENTICATION_TOPIC,
            (AuthenticationNotifier) () -> displayWebSearchResultsCheckBox.setEnabled(true));
  }

  private JPanel createOpenAIServiceSectionPanel() {
    var requestConfigurationPanel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(openAIOrganizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.section.service.openai.organizationField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.service.openai.organizationField.comment")))
        .add(UI.PanelFactory.panel(openAIBaseHostField)
            .withLabel("Base host:")
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIPathField)
            .withLabel("Path:")
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
            .withLabel("Model:")
            .resizeX(false))
        .createPanel();

    var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.section.integration.apiKeyField.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get("settingsConfigurable.section.integration.apiKeyField.comment"))
        .withCommentHyperlinkListener(SwingUtils::handleHyperlinkClicked)
        .createPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Authentication"))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .addComponent(new TitledSeparator("Request Configuration"))
        .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JPanel createAzureServiceSectionPanel() {
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
                "settingsConfigurable.section.service.azure.resourceNameField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.service.azure.resourceNameField.comment")))
        .add(UI.PanelFactory.panel(azureDeploymentIdField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.section.service.azure.deploymentIdField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.service.azure.deploymentIdField.comment")))
        .add(UI.PanelFactory.panel(azureApiVersionField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.section.service.azure.apiVersionField.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.service.azure.apiVersionField.comment")))
        .add(UI.PanelFactory.panel(azureBaseHostField)
            .withLabel("Base host:")
            .resizeX(false))
        .add(UI.PanelFactory.panel(azurePathField)
            .withLabel("Path:")
            .resizeX(false))
        .add(UI.PanelFactory.panel(azureCompletionModelComboBox)
            .withLabel("Model:")
            .resizeX(false))
        .createPanel());

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Authentication"))
        .addComponent(authPanel)
        .addComponent(new TitledSeparator("Request Configuration"))
        .addComponent(configPanel)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JPanel createYouServiceSectionPanel() {
    return FormBuilder.createFormBuilder()
        .addComponent(new YouServiceSelectionForm(parentDisposable))
        .addComponent(new TitledSeparator("Chat Preferences"))
        .addComponent(withEmptyLeftBorder(displayWebSearchResultsCheckBox))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
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

  private void registerRadioButtons(List<Map.Entry<JBRadioButton, JPanel>> entries) {
    var buttonGroup = new ButtonGroup();
    entries.forEach(entry -> buttonGroup.add(entry.getKey()));
    entries.forEach(entry -> entry.getKey().addActionListener((e) -> {
      for (Map.Entry<JBRadioButton, JPanel> innerEntry : entries) {
        innerEntry.getValue().setVisible(innerEntry.equals(entry));
      }
    }));
  }

  public void setOpenAIApiKey(String apiKey) {
    openAIApiKeyField.setText(apiKey);
  }

  public String getOpenAIApiKey() {
    return new String(openAIApiKeyField.getPassword());
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

  public void setOpenAIModel(String model) {
    openAICompletionModelComboBox.setSelectedItem(OpenAIChatCompletionModel.findByCode(model));
  }

  public String getOpenAIModel() {
    return ((OpenAIChatCompletionModel) (openAICompletionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
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

  public void setAzureModel(String model) {
    azureCompletionModelComboBox.setSelectedItem(OpenAIChatCompletionModel.findByCode(model));
  }

  public String getAzureModel() {
    return ((OpenAIChatCompletionModel) (azureCompletionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
  }

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    displayWebSearchResultsCheckBox.setSelected(displayWebSearchResults);
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResultsCheckBox.isSelected();
  }

  public void setLlamaModel(LlamaModel model) {
    llamaServiceSectionPanel.setSelectedModel(model);
  }

  public LlamaModel getLlamaModel() {
    return llamaServiceSectionPanel.getSelectedModel();
  }

  public void setLlamaModelPath(String modelPath) {
    llamaServiceSectionPanel.setModelDestinationPath(modelPath);
  }

  public String getLlamaModelPath() {
    return llamaServiceSectionPanel.getModelDestinationPath();
  }

  public void setOpenAIPath(String path) {
    openAIPathField.setText(path);
  }

  public String getOpenAIPath() {
    return openAIPathField.getText();
  }

  public void setAzurePath(String path) {
    azurePathField.setText(path);
  }

  public String getAzurePath() {
    return azurePathField.getText();
  }

  public void setLlamaServerPort(int serverPort) {
    llamaServiceSectionPanel.setServerPort(serverPort);
  }

  public int getLlamaServerPort() {
    return llamaServiceSectionPanel.getServerPort();
  }

  public JPanel getOpenAIServiceSectionPanel() {
    return openAIServiceSectionPanel;
  }

  public JPanel getAzureServiceSectionPanel() {
    return azureServiceSectionPanel;
  }

  public JPanel getYouServiceSectionPanel() {
    return youServiceSectionPanel;
  }

  public JPanel getLlamaServiceSectionPanel() {
    return llamaServiceSectionPanel;
  }
}
