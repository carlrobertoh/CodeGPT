package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.credentials.UserCredentialsManager;
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
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getEmail().equals(settings.email) ||
        !settingsComponent.getDisplayName().equals(settings.displayName) ||

        serviceSelectionForm.isOpenAIServiceSelected() != settings.useOpenAIService ||
        serviceSelectionForm.isAzureServiceSelected() != settings.useAzureService ||
        !serviceSelectionForm.getOpenAIApiKey().equals(OpenAICredentialsManager.getInstance().getApiKey()) ||
        !serviceSelectionForm.getOpenAIOrganization().equals(settings.openAIOrganization) ||
        !serviceSelectionForm.getOpenAIBaseHost().equals(settings.openAIBaseHost) ||

        serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected() != settings.useAzureActiveDirectoryAuthentication ||
        serviceSelectionForm.isAzureApiKeyAuthenticationSelected() != settings.useAzureApiKeyAuthentication ||
        !serviceSelectionForm.getAzureActiveDirectoryToken().equals(AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken()) ||
        !serviceSelectionForm.getAzureOpenAIApiKey().equals(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey()) ||
        !serviceSelectionForm.getAzureResourceName().equals(settings.azureResourceName) ||
        !serviceSelectionForm.getAzureDeploymentId().equals(settings.azureDeploymentId) ||
        !serviceSelectionForm.getAzureApiVersion().equals(settings.azureApiVersion) ||
        !serviceSelectionForm.getAzureBaseHost().equals(settings.azureBaseHost) ||

        isModelChanged(settings) ||
        isCompletionOptionChanged(settings);
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();
    var isModelChanged = isModelChanged(settings);

    if (isModelChanged) {
      EncodingManager.getInstance()
          .setEncoding(settings.isChatCompletionOptionSelected ? settings.chatCompletionBaseModel
              : settings.textCompletionBaseModel);
    }

    if (isCompletionOptionChanged(settings) || isModelChanged) {
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

    settings.email = settingsComponent.getEmail();
    settings.displayName = settingsComponent.getDisplayName();

    settings.useOpenAIService = serviceSelectionForm.isOpenAIServiceSelected();
    settings.useAzureService = serviceSelectionForm.isAzureServiceSelected();

    settings.openAIOrganization = serviceSelectionForm.getOpenAIOrganization();
    settings.openAIBaseHost = serviceSelectionForm.getOpenAIBaseHost();

    settings.useAzureActiveDirectoryAuthentication = serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected();
    settings.useAzureApiKeyAuthentication = serviceSelectionForm.isAzureApiKeyAuthenticationSelected();
    settings.azureResourceName = serviceSelectionForm.getAzureResourceName();
    settings.azureDeploymentId = serviceSelectionForm.getAzureDeploymentId();
    settings.azureApiVersion = serviceSelectionForm.getAzureApiVersion();
    settings.azureBaseHost = serviceSelectionForm.getAzureBaseHost();

    settings.chatCompletionBaseModel = modelSelectionForm.getChatCompletionBaseModel().getCode();
    settings.textCompletionBaseModel = modelSelectionForm.getTextCompletionBaseModel().getCode();
    settings.isChatCompletionOptionSelected = modelSelectionForm.isChatCompletionOptionSelected();
    settings.isTextCompletionOptionSelected = modelSelectionForm.isTextCompletionOptionSelected();
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();

    settingsComponent.setEmail(settings.email);
    settingsComponent.setDisplayName(settings.displayName);

    serviceSelectionForm.setOpenAIServiceSelected(settings.useAzureService);
    serviceSelectionForm.setAzureServiceSelected(settings.useAzureService);

    serviceSelectionForm.setOpenAIApiKey(OpenAICredentialsManager.getInstance().getApiKey());
    serviceSelectionForm.setOpenAIOrganization(settings.openAIOrganization);
    serviceSelectionForm.setOpenAIBaseHost(settings.openAIBaseHost);

    serviceSelectionForm.setAzureApiKeyAuthenticationSelected(settings.useAzureApiKeyAuthentication);
    serviceSelectionForm.setAzureApiKey(AzureCredentialsManager.getInstance().getAzureOpenAIApiKey());
    serviceSelectionForm.setAzureActiveDirectoryAuthenticationSelected(settings.useAzureActiveDirectoryAuthentication);
    serviceSelectionForm.setAzureActiveDirectoryToken(AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken());
    serviceSelectionForm.setAzureResourceName(settings.azureResourceName);
    serviceSelectionForm.setAzureDeploymentId(settings.azureDeploymentId);
    serviceSelectionForm.setAzureApiVersion(settings.azureApiVersion);
    serviceSelectionForm.setAzureBaseHost(settings.azureBaseHost);

    modelSelectionForm.setUseChatCompletionSelected(settings.isChatCompletionOptionSelected);
    modelSelectionForm.setUseTextCompletionSelected(settings.isTextCompletionOptionSelected);
    modelSelectionForm.setChatCompletionBaseModel(settings.chatCompletionBaseModel);
    modelSelectionForm.setTextCompletionBaseModel(settings.textCompletionBaseModel);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  private boolean isCompletionOptionChanged(SettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return modelSelectionForm.isChatCompletionOptionSelected() != settings.isChatCompletionOptionSelected ||
        modelSelectionForm.isTextCompletionOptionSelected() != settings.isTextCompletionOptionSelected;
  }

  private boolean isModelChanged(SettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return !modelSelectionForm.getChatCompletionBaseModel().getCode().equals(settings.chatCompletionBaseModel) ||
        !modelSelectionForm.getTextCompletionBaseModel().getCode().equals(settings.textCompletionBaseModel);
  }

  @Override
  public void dispose() {
  }
}
