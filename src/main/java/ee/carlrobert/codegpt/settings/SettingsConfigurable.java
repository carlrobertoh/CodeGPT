package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.credentials.UserCredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import ee.carlrobert.codegpt.util.ApplicationUtils;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable implements Configurable, Disposable {

  private SettingsComponent settingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("settings.displayName");
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return settingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var settings = SettingsState.getInstance();
    settingsComponent = new SettingsComponent(this, settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var modelSettings = ModelSettingsState.getInstance();

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getEmail().equals(settings.getEmail()) ||
        !settingsComponent.getDisplayName().equals(settings.getDisplayName()) ||

        serviceSelectionForm.isOpenAIServiceSelected() != settings.isUseOpenAIService() ||
        serviceSelectionForm.isAzureServiceSelected() != settings.isUseAzureService() ||
        !serviceSelectionForm.getOpenAIApiKey().equals(OpenAICredentialsManager.getInstance().getApiKey()) ||
        !serviceSelectionForm.getOpenAIOrganization().equals(openAISettings.getOrganization()) ||
        !serviceSelectionForm.getOpenAIBaseHost().equals(openAISettings.getBaseHost()) ||

        serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected() != azureSettings.isUseAzureActiveDirectoryAuthentication() ||
        serviceSelectionForm.isAzureApiKeyAuthenticationSelected() != azureSettings.isUseAzureApiKeyAuthentication() ||
        !serviceSelectionForm.getAzureActiveDirectoryToken().equals(AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken()) ||
        !serviceSelectionForm.getAzureOpenAIApiKey().equals(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey()) ||
        !serviceSelectionForm.getAzureResourceName().equals(azureSettings.getResourceName()) ||
        !serviceSelectionForm.getAzureDeploymentId().equals(azureSettings.getDeploymentId()) ||
        !serviceSelectionForm.getAzureApiVersion().equals(azureSettings.getApiVersion()) ||
        !serviceSelectionForm.getAzureBaseHost().equals(azureSettings.getBaseHost()) ||

        isModelChanged(modelSettings) ||
        isCompletionOptionChanged(modelSettings);
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var modelSettings = ModelSettingsState.getInstance();
    var isModelChanged = isModelChanged(modelSettings);

    if (isModelChanged) {
      EncodingManager.getInstance().setEncoding(modelSettings.isUseChatCompletion() ?
          modelSettings.getChatCompletionModel() :
          modelSettings.getTextCompletionModel());
    }

    if (isCompletionOptionChanged(modelSettings) || isModelChanged) {
      ConversationsState.getInstance().setCurrentConversation(null);
      var project = ApplicationUtils.findCurrentProject();
      if (project == null) {
        throw new RuntimeException("Could not find current project.");
      }

      StandardChatToolWindowContentManager.getInstance(project)
          .tryFindChatTabbedPane()
          .ifPresent(tabbedPane -> {
            tabbedPane.clearAll();
            var tabPanel = new StandardChatToolWindowTabPanel(project);
            tabPanel.displayLandingView();
            tabbedPane.addNewTab(tabPanel);
          });
    }

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();

    UserCredentialsManager.getInstance().setAccountPassword(settingsComponent.getPassword());
    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance().setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    settings.setEmail(settingsComponent.getEmail());
    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setUseOpenAIService(serviceSelectionForm.isOpenAIServiceSelected());
    settings.setUseOpenAIService(serviceSelectionForm.isAzureServiceSelected());

    openAISettings.setOrganization(serviceSelectionForm.getOpenAIOrganization());
    openAISettings.setBaseHost(serviceSelectionForm.getOpenAIBaseHost());

    azureSettings.setUseAzureActiveDirectoryAuthentication(serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected());
    azureSettings.setUseAzureApiKeyAuthentication(serviceSelectionForm.isAzureApiKeyAuthenticationSelected());
    azureSettings.setResourceName(serviceSelectionForm.getAzureResourceName());
    azureSettings.setDeploymentId(serviceSelectionForm.getAzureDeploymentId());
    azureSettings.setApiVersion(serviceSelectionForm.getAzureApiVersion());
    azureSettings.setBaseHost(serviceSelectionForm.getAzureBaseHost());

    modelSettings.setUseChatCompletion(modelSelectionForm.isChatCompletionOptionSelected());
    modelSettings.setUseTextCompletion(modelSelectionForm.isTextCompletionOptionSelected());
    modelSettings.setChatCompletionModel(modelSelectionForm.getChatCompletionBaseModel().getCode());
    modelSettings.setTextCompletionModel(modelSelectionForm.getTextCompletionBaseModel().getCode());
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var modelSettings = ModelSettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();

    settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());

    serviceSelectionForm.setOpenAIServiceSelected(settings.isUseOpenAIService());
    serviceSelectionForm.setAzureServiceSelected(settings.isUseAzureService());

    serviceSelectionForm.setOpenAIApiKey(OpenAICredentialsManager.getInstance().getApiKey());
    serviceSelectionForm.setOpenAIOrganization(openAISettings.getOrganization());
    serviceSelectionForm.setOpenAIBaseHost(openAISettings.getBaseHost());

    serviceSelectionForm.setAzureApiKeyAuthenticationSelected(azureSettings.isUseAzureApiKeyAuthentication());
    serviceSelectionForm.setAzureApiKey(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey());
    serviceSelectionForm.setAzureActiveDirectoryAuthenticationSelected(azureSettings.isUseAzureActiveDirectoryAuthentication());
    serviceSelectionForm.setAzureActiveDirectoryToken(AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken());
    serviceSelectionForm.setAzureResourceName(azureSettings.getResourceName());
    serviceSelectionForm.setAzureDeploymentId(azureSettings.getDeploymentId());
    serviceSelectionForm.setAzureApiVersion(azureSettings.getApiVersion());
    serviceSelectionForm.setAzureBaseHost(azureSettings.getBaseHost());

    modelSelectionForm.setUseChatCompletionSelected(modelSettings.isUseChatCompletion());
    modelSelectionForm.setUseTextCompletionSelected(modelSettings.isUseTextCompletion());
    modelSelectionForm.setChatCompletionBaseModel(modelSettings.getChatCompletionModel());
    modelSelectionForm.setTextCompletionBaseModel(modelSettings.getTextCompletionModel());
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  private boolean isCompletionOptionChanged(ModelSettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return modelSelectionForm.isChatCompletionOptionSelected() != settings.isUseChatCompletion() ||
        modelSelectionForm.isTextCompletionOptionSelected() != settings.isUseTextCompletion();
  }

  private boolean isModelChanged(ModelSettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return !modelSelectionForm.getChatCompletionBaseModel().getCode().equals(settings.getChatCompletionModel()) ||
        !modelSelectionForm.getTextCompletionBaseModel().getCode().equals(settings.getTextCompletionModel());
  }

  @Override
  public void dispose() {
  }
}
