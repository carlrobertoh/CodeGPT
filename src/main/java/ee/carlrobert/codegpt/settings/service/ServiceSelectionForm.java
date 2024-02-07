package ee.carlrobert.codegpt.settings.service;

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
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationNotifier;
import ee.carlrobert.codegpt.credentials.AzureCredentialManager;
import ee.carlrobert.codegpt.credentials.LlamaCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsState;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaModelPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaRequestPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaServerPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.service.you.YouServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
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

  private final JPanel youServiceSectionPanel;
  private final JBCheckBox displayWebSearchResultsCheckBox;

  private final LlamaServiceSelectionForm llamaServiceSectionPanel;

  public ServiceSelectionForm(Disposable parentDisposable) {
    this.parentDisposable = parentDisposable;
    openAIApiKeyField = new JBPasswordField();
    openAIApiKeyField.setColumns(30);
    openAIApiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());

    var azureSettings = AzureSettings.getCurrentState();
    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useApiKeyAuth.label"),
        azureSettings.isUseAzureApiKeyAuthentication());
    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.service.azure.useActiveDirectoryAuth.label"),
        azureSettings.isUseAzureActiveDirectoryAuthentication());
    azureApiKeyField = new JBPasswordField();
    azureApiKeyField.setColumns(30);
    azureApiKeyField.setText(AzureCredentialManager.getInstance().getApiKey());
    azureApiKeyFieldPanel = UI.PanelFactory.panel(azureApiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .createPanel();
    azureActiveDirectoryTokenField = new JBPasswordField();
    azureActiveDirectoryTokenField.setColumns(30);
    azureActiveDirectoryTokenField.setText(
        AzureCredentialManager.getInstance().getActiveDirectoryToken());
    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.azure.bearerToken.label"))
        .resizeX(false)
        .createPanel();

    var openAISettings = OpenAISettings.getCurrentState();
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

    displayWebSearchResultsCheckBox = new JBCheckBox(
        CodeGPTBundle.get("settingsConfigurable.service.you.displayResults.label"),
        YouSettings.getCurrentState().isDisplayWebSearchResults());

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
        .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.model.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIOrganizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")))
        .add(UI.PanelFactory.panel(openAIBaseHostField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.baseHost.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIPathField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.path.label"))
            .resizeX(false))
        .createPanel();

    var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
        .createPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
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
                "settingsConfigurable.service.azure.apiVersion.label"))
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

  private JPanel createYouServiceSectionPanel() {
    return FormBuilder.createFormBuilder()
        .addComponent(new YouServiceSelectionForm(parentDisposable))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.you.chatPreferences.title")))
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

  public OpenAISettingsState getCurrentOpenAIFormState() {
    var state = new OpenAISettingsState();
    state.setOrganization(getOpenAIOrganization());
    state.setBaseHost(getOpenAIBaseHost());
    state.setPath(getOpenAIPath());
    state.setModel(getOpenAIModel());
    return state;
  }

  public void resetOpenAIForm() {
    var state = OpenAISettings.getCurrentState();
    setOpenAIApiKey(OpenAICredentialManager.getInstance().getCredential());
    setOpenAIOrganization(state.getOrganization());
    setOpenAIBaseHost(state.getBaseHost());
    setOpenAIPath(state.getPath());
    setOpenAIModel(state.getModel());
  }

  public AzureSettingsState getCurrentAzureFormState() {
    var state = new AzureSettingsState();
    state.setUseAzureActiveDirectoryAuthentication(isAzureActiveDirectoryAuthenticationSelected());
    state.setUseAzureApiKeyAuthentication(isAzureApiKeyAuthenticationSelected());
    state.setResourceName(getAzureResourceName());
    state.setDeploymentId(getAzureDeploymentId());
    state.setApiVersion(getAzureApiVersion());
    state.setBaseHost(getAzureBaseHost());
    state.setPath(getAzurePath());
    return state;
  }

  public void resetAzureForm() {
    var state = AzureSettings.getCurrentState();
    setAzureApiKey(AzureCredentialManager.getInstance().getApiKey());
    setAzureActiveDirectoryToken(AzureCredentialManager.getInstance().getActiveDirectoryToken());
    setAzureApiKeyAuthenticationSelected(state.isUseAzureApiKeyAuthentication());
    setAzureActiveDirectoryAuthenticationSelected(state.isUseAzureActiveDirectoryAuthentication());
    setAzureResourceName(state.getResourceName());
    setAzureDeploymentId(state.getDeploymentId());
    setAzureApiVersion(state.getApiVersion());
    setAzureBaseHost(state.getBaseHost());
    setAzurePath(state.getPath());
  }

  public LlamaSettingsState getCurrentLlamaFormState() {
    var state = new LlamaSettingsState();
    var modelPreferencesForm = getLlamaModelPreferencesForm();
    state.setCustomLlamaModelPath(modelPreferencesForm.getCustomLlamaModelPath());
    state.setHuggingFaceModel(modelPreferencesForm.getSelectedModel());
    state.setUseCustomModel(modelPreferencesForm.isUseCustomLlamaModel());
    state.setLocalModelPromptTemplate(modelPreferencesForm.getPromptTemplate());
    state.setRemoteModelPromptTemplate(getLlamaPromptTemplate());
    state.setLocalModelInfillPromptTemplate(modelPreferencesForm.getInfillPromptTemplate());
    state.setRemoteModelInfillPromptTemplate(
        getLlamaServerPreferencesForm().getInfillPromptTemplate());
    var requestPreferencesForm = getLlamaRequestPreferencesForm();
    state.setTopK(requestPreferencesForm.getTopK());
    state.setTopP(requestPreferencesForm.getTopP());
    state.setMinP(requestPreferencesForm.getMinP());
    state.setRepeatPenalty(requestPreferencesForm.getRepeatPenalty());
    state.setRunLocalServer(isLlamaRunLocalServer());
    state.setBaseHost(getLlamaBaseHost());
    state.setServerPort(getLlamaServerPort());
    state.setContextSize(getContextSize());
    state.setThreads(getThreads());
    state.setAdditionalParameters(getAdditionalParameters());
    return state;
  }

  public void resetLlamaForm() {
    var state = LlamaSettings.getCurrentState();
    var modelPreferencesForm = getLlamaModelPreferencesForm();
    modelPreferencesForm.setSelectedModel(state.getHuggingFaceModel());
    modelPreferencesForm.setCustomLlamaModelPath(state.getCustomLlamaModelPath());
    modelPreferencesForm.setUseCustomLlamaModel(state.isUseCustomModel());
    modelPreferencesForm.setPromptTemplate(state.getLocalModelPromptTemplate());
    modelPreferencesForm.setInfillPromptTemplate(state.getLocalModelInfillPromptTemplate());
    var requestPreferencesForm = getLlamaRequestPreferencesForm();
    requestPreferencesForm.setTopK(state.getTopK());
    requestPreferencesForm.setTopP(state.getTopP());
    requestPreferencesForm.setMinP(state.getMinP());
    requestPreferencesForm.setRepeatPenalty(state.getRepeatPenalty());
    setLlamaRunLocalServer(state.isRunLocalServer());
    setLlamaBaseHost(state.getBaseHost());
    setLlamaServerPort(state.getServerPort());
    setLlamaPromptTemplate(state.getRemoteModelPromptTemplate());
    setContextSize(state.getContextSize());
    setThreads(state.getThreads());
    setAdditionalParameters(state.getAdditionalParameters());
    var llamaServerPreferencesForm = getLlamaServerPreferencesForm();
    llamaServerPreferencesForm.setInfillPromptTemplate(state.getRemoteModelInfillPromptTemplate());
    llamaServerPreferencesForm.setApiKey(LlamaCredentialManager.getInstance().getCredential());
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

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    displayWebSearchResultsCheckBox.setSelected(displayWebSearchResults);
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResultsCheckBox.isSelected();
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServiceSectionPanel.getLlamaServerPreferencesForm();
  }

  public LlamaModelPreferencesForm getLlamaModelPreferencesForm() {
    return llamaServiceSectionPanel.getLlamaModelPreferencesForm();
  }

  public LlamaRequestPreferencesForm getLlamaRequestPreferencesForm() {
    return llamaServiceSectionPanel.getLlamaRequestPreferencesForm();
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

  public void setLlamaRunLocalServer(boolean runLocalServer) {
    llamaServiceSectionPanel.setRunLocalServer(runLocalServer);
  }

  public boolean isLlamaRunLocalServer() {
    return llamaServiceSectionPanel.isRunLocalServer();
  }

  public void setLlamaBaseHost(String baseHost) {
    llamaServiceSectionPanel.setBaseHost(baseHost);
  }

  public String getLlamaBaseHost() {
    return llamaServiceSectionPanel.getBaseHost();
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

  public int getContextSize() {
    return llamaServiceSectionPanel.getContextSize();
  }

  public void setContextSize(int contextSize) {
    llamaServiceSectionPanel.setContextSize(contextSize);
  }

  public int getThreads() {
    return llamaServiceSectionPanel.getThreads();
  }

  public void setThreads(int threads) {
    llamaServiceSectionPanel.setThreads(threads);
  }

  public String getAdditionalParameters() {
    return llamaServiceSectionPanel.getAdditionalParameters();
  }

  public void setAdditionalParameters(String additionalParameters) {
    llamaServiceSectionPanel.setAdditionalParameters(additionalParameters);
  }

  public PromptTemplate getLlamaPromptTemplate() {
    return getLlamaServerPreferencesForm().getPromptTemplate();
  }

  public void setLlamaPromptTemplate(PromptTemplate promptTemplate) {
    getLlamaServerPreferencesForm().setPromptTemplate(promptTemplate);
  }
}
