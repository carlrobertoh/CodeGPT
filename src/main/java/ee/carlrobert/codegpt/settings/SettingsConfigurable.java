package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
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

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName()) ||
        isServiceChanged(serviceSelectionForm, settings) ||
        openAISettings.isModified(serviceSelectionForm) ||
        azureSettings.isModified(serviceSelectionForm) ||
        serviceSelectionForm.isDisplayWebSearchResults() != settings.isDisplayWebSearchResults();
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();

    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance().setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setUseOpenAIService(serviceSelectionForm.isOpenAIServiceSelected());
    settings.setUseAzureService(serviceSelectionForm.isAzureServiceSelected());
    settings.setUseYouService(serviceSelectionForm.isCustomServiceSelected());
    settings.setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    openAISettings.apply(serviceSelectionForm);
    azureSettings.apply(serviceSelectionForm);

    if (isServiceChanged(serviceSelectionForm, settings)) {
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

    openAISettings.reset(serviceSelectionForm);
    azureSettings.reset(serviceSelectionForm);

    serviceSelectionForm.setDisplayWebSearchResults(settings.isDisplayWebSearchResults());
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  @Override
  public void dispose() {
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
