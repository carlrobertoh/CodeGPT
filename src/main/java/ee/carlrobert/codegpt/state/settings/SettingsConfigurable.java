package ee.carlrobert.codegpt.state.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.ProjectUtil;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ToolWindowTabPanelFactory;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable implements Configurable {

  private SettingsComponent settingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeGPT: Settings";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return settingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var settings = SettingsState.getInstance();
    settingsComponent = new SettingsComponent(settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = SettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return !settingsComponent.getApiKey().equals(settings.apiKey) ||
        !settingsComponent.getDisplayName().equals(settings.displayName) ||
        settingsComponent.isUseApiKeyFromEnvVar() != settings.useApiKeyFromEnvVar ||
        settingsComponent.isUseOpenAIAccountName() != settings.useOpenAIAccountName ||
        serviceSelectionForm.isUseOpenAIService() != settings.useOpenAIService ||
        serviceSelectionForm.isUseAzureService() != settings.useAzureService ||
        serviceSelectionForm.isUseCustomService() != settings.useCustomService ||
        serviceSelectionForm.isUseActiveDirectoryAuthentication() !=
            settings.useActiveDirectoryAuthentication ||
        !serviceSelectionForm.getCustomHost().equals(settings.customHost) ||
        !serviceSelectionForm.getResourceName().equals(settings.resourceName) ||
        !serviceSelectionForm.getDeploymentId().equals(settings.deploymentId) ||
        !serviceSelectionForm.getApiVersion().equals(settings.apiVersion) ||
        !serviceSelectionForm.getOrganization().equals(settings.organization) ||
        !modelSelectionForm.getCustomChatCompletionModel()
            .equals(settings.customChatCompletionModel) ||
        !modelSelectionForm.getCustomTextCompletionModel()
            .equals(settings.customTextCompletionModel) ||
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
      var project = ProjectUtil.guessCurrentProject(
          settingsComponent.getPanel()); // TODO: Find a better way
      project.getService(ChatContentManagerService.class)
          .tryFindChatTabbedPane()
          .ifPresent(tabbedPane -> {
            tabbedPane.clearAll();
            var tabPanel = ToolWindowTabPanelFactory.getTabPanel(project);
            tabPanel.displayLandingView();
            tabbedPane.addNewTab(tabPanel);
          });
    }

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();

    settings.apiKey = settingsComponent.getApiKey();
    settings.displayName = settingsComponent.getDisplayName();
    settings.useApiKeyFromEnvVar = settingsComponent.isUseApiKeyFromEnvVar();
    settings.useOpenAIAccountName = settingsComponent.isUseOpenAIAccountName();
    settings.useOpenAIService = serviceSelectionForm.isUseOpenAIService();
    settings.useAzureService = serviceSelectionForm.isUseAzureService();
    settings.useCustomService = serviceSelectionForm.isUseCustomService();
    settings.customHost = serviceSelectionForm.getCustomHost();
    settings.useActiveDirectoryAuthentication = serviceSelectionForm.isUseActiveDirectoryAuthentication();
    settings.resourceName = serviceSelectionForm.getResourceName();
    settings.deploymentId = serviceSelectionForm.getDeploymentId();
    settings.apiVersion = serviceSelectionForm.getApiVersion();
    settings.organization = serviceSelectionForm.getOrganization();
    settings.chatCompletionBaseModel = modelSelectionForm.getChatCompletionBaseModel().getCode();
    settings.textCompletionBaseModel = modelSelectionForm.getTextCompletionBaseModel().getCode();
    settings.customChatCompletionModel = modelSelectionForm.getCustomChatCompletionModel();
    settings.customTextCompletionModel = modelSelectionForm.getCustomTextCompletionModel();
    settings.isChatCompletionOptionSelected = modelSelectionForm.isChatCompletionOptionSelected();
    settings.isTextCompletionOptionSelected = modelSelectionForm.isTextCompletionOptionSelected();
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var modelSelectionForm = settingsComponent.getModelSelectionForm();

    settingsComponent.setApiKey(settings.apiKey);
    settingsComponent.setUseApiKeyFromEnvVarCheckBox(settings.useApiKeyFromEnvVar);
    settingsComponent.setDisplayName(settings.displayName);
    settingsComponent.setUseOpenAIAccountNameCheckBox(settings.useOpenAIAccountName);
    serviceSelectionForm.setUseOpenAIServiceSelected(settings.useAzureService);
    serviceSelectionForm.setUseAzureServiceSelected(settings.useAzureService);
    serviceSelectionForm.setUseCustomServiceSelected(settings.useCustomService);
    serviceSelectionForm.setCustomHost(settings.customHost);
    serviceSelectionForm.setUseActiveDirectoryAuthenticationSelected(
        settings.useActiveDirectoryAuthentication);
    serviceSelectionForm.setResourceName(settings.resourceName);
    serviceSelectionForm.setDeploymentId(settings.deploymentId);
    serviceSelectionForm.setApiVersionField(settings.apiVersion);
    serviceSelectionForm.setOrganization(settings.organization);
    modelSelectionForm.setUseChatCompletionSelected(settings.isChatCompletionOptionSelected);
    modelSelectionForm.setUseTextCompletionSelected(settings.isTextCompletionOptionSelected);
    modelSelectionForm.setChatCompletionBaseModel(settings.chatCompletionBaseModel);
    modelSelectionForm.setTextCompletionBaseModel(settings.textCompletionBaseModel);
    modelSelectionForm.setCustomChatCompletionModel(settings.customChatCompletionModel);
    modelSelectionForm.setCustomTextCompletionModel(settings.customTextCompletionModel);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  private boolean isCompletionOptionChanged(SettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return modelSelectionForm.isChatCompletionOptionSelected()
        != settings.isChatCompletionOptionSelected ||
        modelSelectionForm.isTextCompletionOptionSelected()
            != settings.isTextCompletionOptionSelected;
  }

  private boolean isModelChanged(SettingsState settings) {
    var modelSelectionForm = settingsComponent.getModelSelectionForm();
    return !modelSelectionForm.getChatCompletionBaseModel().getCode()
        .equals(settings.chatCompletionBaseModel) ||
        !modelSelectionForm.getTextCompletionBaseModel().getCode()
            .equals(settings.textCompletionBaseModel);
  }
}
