package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.ApplicationUtils;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
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

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName()) ||
        isServiceChanged(serviceSelectionForm, settings) ||

        isModelChanged(ModelSettingsState.getInstance()) ||

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

        serviceSelectionForm.isDisplayWebSearchResults() != settings.isDisplayWebSearchResults();
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var modelSettings = ModelSettingsState.getInstance();
    var selectedCompletionModel = serviceSelectionForm.getSelectedCompletionModel();

    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance().setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    settings.setDisplayName(settingsComponent.getDisplayName());

    settings.setUseOpenAIService(serviceSelectionForm.isOpenAIServiceSelected());
    settings.setUseAzureService(serviceSelectionForm.isAzureServiceSelected());
    settings.setUseYouService(serviceSelectionForm.isCustomServiceSelected());

    settings.setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    openAISettings.setOrganization(serviceSelectionForm.getOpenAIOrganization());
    openAISettings.setBaseHost(serviceSelectionForm.getOpenAIBaseHost());

    azureSettings.setUseAzureActiveDirectoryAuthentication(serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected());
    azureSettings.setUseAzureApiKeyAuthentication(serviceSelectionForm.isAzureApiKeyAuthenticationSelected());
    azureSettings.setResourceName(serviceSelectionForm.getAzureResourceName());
    azureSettings.setDeploymentId(serviceSelectionForm.getAzureDeploymentId());
    azureSettings.setApiVersion(serviceSelectionForm.getAzureApiVersion());
    azureSettings.setBaseHost(serviceSelectionForm.getAzureBaseHost());

    modelSettings.setChatCompletionModel(selectedCompletionModel.getCode());

    if (isModelChanged(modelSettings) || isServiceChanged(serviceSelectionForm, settings)) {
      resetActiveTab();
    }
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());

    serviceSelectionForm.setOpenAIServiceSelected(settings.isUseOpenAIService());
    serviceSelectionForm.setAzureServiceSelected(settings.isUseAzureService());
    serviceSelectionForm.setCustomServiceSelected(settings.isUseYouService());

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

    serviceSelectionForm.setDisplayWebSearchResults(settings.isDisplayWebSearchResults());

    if (!settings.isUseYouService()) {
      serviceSelectionForm.setSelectedChatCompletionModel(
          OpenAIChatCompletionModel.findByCode(ModelSettingsState.getInstance().getChatCompletionModel()));
    }
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  @Override
  public void dispose() {
  }

  private boolean isModelChanged(ModelSettingsState settings) {
    return !settingsComponent.getServiceSelectionForm().getSelectedCompletionModel().getCode().equals(settings.getChatCompletionModel());
  }

  private boolean isServiceChanged(ServiceSelectionForm serviceSelectionForm, SettingsState settings) {
    return serviceSelectionForm.isOpenAIServiceSelected() != settings.isUseOpenAIService() ||
        serviceSelectionForm.isAzureServiceSelected() != settings.isUseAzureService() ||
        serviceSelectionForm.isCustomServiceSelected() != settings.isUseYouService();
  }

  private void resetActiveTab() {
    ConversationsState.getInstance().setCurrentConversation(null);
    var project = ApplicationUtils.findCurrentProject();
    if (project == null) {
      throw new RuntimeException("Could not find current project.");
    }

    StandardChatToolWindowContentManager.getInstance(project).resetActiveTab();
  }
}
