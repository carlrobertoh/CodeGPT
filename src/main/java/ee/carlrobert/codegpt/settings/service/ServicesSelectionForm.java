package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.io.FileUtil;
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
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationNotifier;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.llama.RequestPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaServerPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.ServiceFormPanel;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServicesSelectionForm {

  private static final Logger LOG = Logger.getInstance(ServicesSelectionForm.class);

  private final Disposable parentDisposable;

  private final OpenAiServiceSelectionForm openAIServiceSectionPanel;
//  private final JBPasswordField openAIApiKeyField;
//  private final JBTextField openAIBaseHostField;
//  private final JBTextField openAIPathField;
//  private final JBTextField openAIOrganizationField;
//  private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

//  private final JBRadioButton useAzureApiKeyAuthenticationRadioButton;
//  private final JBPasswordField azureApiKeyField;
//  private final JPanel azureApiKeyFieldPanel;
//  private final JBRadioButton useAzureActiveDirectoryAuthenticationRadioButton;
//  private final JBPasswordField azureActiveDirectoryTokenField;
//  private final JPanel azureActiveDirectoryTokenFieldPanel;
//  private final JBTextField azureBaseHostField;
//  private final JBTextField azurePathField;
//  private final JBTextField azureResourceNameField;
//  private final JBTextField azureDeploymentIdField;
//  private final JBTextField azureApiVersionField;
  private final AzureServiceSelectionForm azureServiceSectionPanel;

  private final JPanel youServiceSectionPanel;
  private final JBCheckBox displayWebSearchResultsCheckBox;

  private final ServiceFormPanel llamaServiceSectionPanel;

  public ServicesSelectionForm(Disposable parentDisposable) {
    this.parentDisposable = parentDisposable;
//    var openAISettings = OpenAISettingsState.getInstance();
//    openAIApiKeyField = new JBPasswordField();
//    openAIApiKeyField.setColumns(30);
//    openAIApiKeyField.setText(openAISettings.getCredentialsManager().getApiKey());

//    var azureSettings = AzureSettingsState.getInstance();
//    useAzureApiKeyAuthenticationRadioButton = new JBRadioButton(
//        CodeGPTBundle.get("settingsConfigurable.service.azure.useApiKeyAuth.label"),
//        azureSettings.isUseAzureApiKeyAuthentication());
//    useAzureActiveDirectoryAuthenticationRadioButton = new JBRadioButton(
//        CodeGPTBundle.get("settingsConfigurable.service.azure.useActiveDirectoryAuth.label"),
//        azureSettings.isUseAzureActiveDirectoryAuthentication());
//    azureApiKeyField = new JBPasswordField();
//    azureApiKeyField.setColumns(30);
//    AzureCredentialsManager credentials = azureSettings.getCredentialsManager();
//    azureApiKeyField.setText(credentials.getApiKey());
//    azureApiKeyFieldPanel = UI.PanelFactory.panel(azureApiKeyField)
//        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
//        .resizeX(false)
//        .createPanel();
//    azureActiveDirectoryTokenField = new JBPasswordField();
//    azureActiveDirectoryTokenField.setColumns(30);
//    azureActiveDirectoryTokenField.setText(credentials.getActiveDirectoryToken());
//    azureActiveDirectoryTokenFieldPanel = UI.PanelFactory.panel(azureActiveDirectoryTokenField)
//        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.azure.bearerToken.label"))
//        .resizeX(false)
//        .createPanel();
//
////    openAIBaseHostField = new JBTextField(openAISettings.getBaseHost(), 30);
////    openAIPathField = new JBTextField(openAISettings.getPath(), 30);
////    openAIOrganizationField = new JBTextField(openAISettings.getOrganization(), 30);
////
////    var selectedOpenAIModel = OpenAIChatCompletionModel.findByCode(openAISettings.getModel());
////
////    openAICompletionModelComboBox = new ComboBox<>(
////        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
////    openAICompletionModelComboBox.setSelectedItem(selectedOpenAIModel);
//
//    azureBaseHostField = new JBTextField(azureSettings.getBaseHost(), 35);
//    azurePathField = new JBTextField(azureSettings.getPath(), 35);
//    azureResourceNameField = new JBTextField(azureSettings.getResourceName(), 35);
//    azureDeploymentIdField = new JBTextField(azureSettings.getDeploymentId(), 35);
//    azureApiVersionField = new JBTextField(azureSettings.getApiVersion(), 35);

    displayWebSearchResultsCheckBox = new JBCheckBox(
        CodeGPTBundle.get("settingsConfigurable.service.you.displayResults.label"),
        YouSettingsState.getInstance().isDisplayWebSearchResults());

    openAIServiceSectionPanel = new OpenAiServiceSelectionForm();
    azureServiceSectionPanel = new AzureServiceSelectionForm();
    youServiceSectionPanel = createYouServiceSectionPanel();
    llamaServiceSectionPanel = createLlamaServiceSectionPanel();

//    registerPanelsVisibility(azureSettings);
//    registerRadioButtons();

    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(AuthenticationNotifier.AUTHENTICATION_TOPIC,
            (AuthenticationNotifier) () -> displayWebSearchResultsCheckBox.setEnabled(true));
  }

  private ServiceFormPanel createLlamaServiceSectionPanel() {
    var settings = LlamaSettingsState.getInstance();
    return new ServiceFormPanel(
        new LlamaServerPreferencesForm(settings,
            ApplicationManager.getApplication().getService(LlamaServerAgent.class), ServiceType.LLAMA) {
          @Override
          public boolean isModelExists(HuggingFaceModel model) {
            return FileUtil.exists(
                CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getModelFileName());
          }
        }, settings.getRequestSettings());
  }

//  private JPanel createOpenAIServiceSectionPanel() {
//    var requestConfigurationPanel = UI.PanelFactory.grid()
//        .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.shared.model.label"))
//            .resizeX(false))
//        .add(UI.PanelFactory.panel(openAIOrganizationField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.service.openai.organization.label"))
//            .resizeX(false)
//            .withComment(CodeGPTBundle.get(
//                "settingsConfigurable.section.openai.organization.comment")))
//        .add(UI.PanelFactory.panel(openAIBaseHostField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.shared.baseHost.label"))
//            .resizeX(false))
//        .add(UI.PanelFactory.panel(openAIPathField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.shared.path.label"))
//            .resizeX(false))
//        .createPanel();
//
//    var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
//        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
//        .resizeX(false)
//        .withComment(
//            CodeGPTBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
//        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
//        .createPanel();
//
//    return FormBuilder.createFormBuilder()
//        .addComponent(new TitledSeparator(
//            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
//        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
//        .addComponent(new TitledSeparator(
//            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
//        .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
//        .addComponentFillVertically(new JPanel(), 0)
//        .getPanel();
//  }
//
//  private JPanel createAzureServiceSectionPanel() {
//    var authPanel = withEmptyLeftBorder(FormBuilder.createFormBuilder()
//        .addComponent(UI.PanelFactory
//            .panel(useAzureApiKeyAuthenticationRadioButton)
//            .resizeX(false)
//            .createPanel())
//        .addComponent(withEmptyLeftBorder(azureApiKeyFieldPanel))
//        .addComponent(UI.PanelFactory
//            .panel(useAzureActiveDirectoryAuthenticationRadioButton)
//            .resizeX(false)
//            .createPanel())
//        .addComponent(withEmptyLeftBorder(azureActiveDirectoryTokenFieldPanel))
//        .getPanel());
//
//    var configPanel = withEmptyLeftBorder(UI.PanelFactory.grid()
//        .add(UI.PanelFactory.panel(azureResourceNameField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.resourceName.label"))
//            .resizeX(false)
//            .withComment(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.resourceName.comment")))
//        .add(UI.PanelFactory.panel(azureDeploymentIdField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.deploymentId.label"))
//            .resizeX(false)
//            .withComment(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.deploymentId.comment")))
//        .add(UI.PanelFactory.panel(azureApiVersionField)
//            .withLabel(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.apiVersion.label"))
//            .resizeX(false)
//            .withComment(CodeGPTBundle.get(
//                "settingsConfigurable.service.azure.apiVersion.comment")))
//        .add(UI.PanelFactory.panel(azureBaseHostField)
//            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"))
//            .resizeX(false))
//        .add(UI.PanelFactory.panel(azurePathField)
//            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.path.label"))
//            .resizeX(false))
//        .createPanel());
//
//    return FormBuilder.createFormBuilder()
//        .addComponent(new TitledSeparator(
//            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
//        .addComponent(authPanel)
//        .addComponent(new TitledSeparator(
//            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
//        .addComponent(configPanel)
//        .addComponentFillVertically(new JPanel(), 0)
//        .getPanel();
//  }

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

//  private void registerPanelsVisibility(AzureSettingsState azureSettings) {
//    azureApiKeyFieldPanel.setVisible(azureSettings.isUseAzureApiKeyAuthentication());
//    azureActiveDirectoryTokenFieldPanel.setVisible(
//        azureSettings.isUseAzureActiveDirectoryAuthentication());
//  }
//
//  private void registerRadioButtons() {
//    registerRadioButtons(List.of(
//        Map.entry(useAzureApiKeyAuthenticationRadioButton, azureApiKeyFieldPanel),
//        Map.entry(useAzureActiveDirectoryAuthenticationRadioButton,
//            azureActiveDirectoryTokenFieldPanel)));
//  }

//  private void registerRadioButtons(List<Map.Entry<JBRadioButton, JPanel>> entries) {
//    var buttonGroup = new ButtonGroup();
//    entries.forEach(entry -> buttonGroup.add(entry.getKey()));
//    entries.forEach(entry -> entry.getKey().addActionListener((e) -> {
//      for (Map.Entry<JBRadioButton, JPanel> innerEntry : entries) {
//        innerEntry.getValue().setVisible(innerEntry.equals(entry));
//      }
//    }));
//  }
//
//  public void setAzureActiveDirectoryAuthenticationSelected(boolean selected) {
//    useAzureActiveDirectoryAuthenticationRadioButton.setSelected(selected);
//  }
//
//  public boolean isAzureActiveDirectoryAuthenticationSelected() {
//    return useAzureActiveDirectoryAuthenticationRadioButton.isSelected();
//  }
//
//  public void setAzureActiveDirectoryToken(String bearerToken) {
//    azureActiveDirectoryTokenField.setText(bearerToken);
//  }
//
//  public String getAzureActiveDirectoryToken() {
//    return new String(azureActiveDirectoryTokenField.getPassword());
//  }
//
//  public void setAzureApiKeyAuthenticationSelected(boolean selected) {
//    useAzureApiKeyAuthenticationRadioButton.setSelected(selected);
//  }
//
//  public boolean isAzureApiKeyAuthenticationSelected() {
//    return useAzureApiKeyAuthenticationRadioButton.isSelected();
//  }
//
//  public void setAzureApiKey(String apiKey) {
//    azureApiKeyField.setText(apiKey);
//  }
//
//  public String getAzureOpenAIApiKey() {
//    return new String(azureApiKeyField.getPassword());
//  }
//
//  public void setAzureResourceName(String resourceName) {
//    azureResourceNameField.setText(resourceName);
//  }
//
//  public String getAzureResourceName() {
//    return azureResourceNameField.getText();
//  }
//
//  public void setAzureDeploymentId(String deploymentId) {
//    azureDeploymentIdField.setText(deploymentId);
//  }
//
//  public String getAzureDeploymentId() {
//    return azureDeploymentIdField.getText();
//  }
//
//  public void setAzureApiVersion(String apiVersion) {
//    azureApiVersionField.setText(apiVersion);
//  }
//
//  public String getAzureApiVersion() {
//    return azureApiVersionField.getText();
//  }
//
//  public void setAzureBaseHost(String baseHost) {
//    azureBaseHostField.setText(baseHost);
//  }
//
//  public String getAzureBaseHost() {
//    return azureBaseHostField.getText();
//  }

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    displayWebSearchResultsCheckBox.setSelected(displayWebSearchResults);
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResultsCheckBox.isSelected();
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServiceSectionPanel.getServerPreferencesForm();
  }

  public RequestPreferencesForm getLlamaRequestPreferencesForm() {
    return llamaServiceSectionPanel.getRequestPreferencesForm();
  }


//  public void setAzurePath(String path) {
//    azurePathField.setText(path);
//  }
//
//  public String getAzurePath() {
//    return azurePathField.getText();
//  }

  public OpenAiServiceSelectionForm getOpenAIServiceSectionPanel() {
    return openAIServiceSectionPanel;
  }

  public AzureServiceSelectionForm getAzureServiceSectionPanel() {
    return azureServiceSectionPanel;
  }

  public JPanel getYouServiceSectionPanel() {
    return youServiceSectionPanel;
  }

  public JPanel getLlamaServiceSectionPanel() {
    return llamaServiceSectionPanel;
  }

}
