package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialManager;
import ee.carlrobert.codegpt.credentials.LlamaCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.GeneralSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;
import ee.carlrobert.codegpt.settings.state.YouSettings;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.ApplicationUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable implements Configurable {

  private Disposable parentDisposable;

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
    var settings = GeneralSettings.getInstance();
    parentDisposable = Disposer.newDisposable();
    settingsComponent = new SettingsComponent(parentDisposable, settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = GeneralSettings.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getState().getDisplayName())
        || isServiceChanged(settings)
        || OpenAISettings.getInstance().isModified(serviceSelectionForm)
        || AzureSettings.getInstance().isModified(serviceSelectionForm)
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettings.getCurrentState().isDisplayWebSearchResults()
        || LlamaSettings.getInstance().isModified(serviceSelectionForm);
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var openAISettings = OpenAISettings.getInstance();

    var prevKey = OpenAICredentialManager.getInstance().getCredential();
    if (prevKey != null && !prevKey.equals(serviceSelectionForm.getOpenAIApiKey())) {
      openAISettings.getState().setOpenAIQuotaExceeded(false);
    }

    OpenAICredentialManager.getInstance().setCredential(serviceSelectionForm.getOpenAIApiKey());
    var azureCredentials = AzureCredentialManager.getInstance();
    azureCredentials.setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    azureCredentials.setActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());
    LlamaCredentialManager.getInstance()
        .setCredential(serviceSelectionForm.getLlamaServerPreferencesForm().getApiKey());

    var settings = GeneralSettings.getInstance();
    settings.getState().setDisplayName(settingsComponent.getDisplayName());
    settings.getState().setSelectedService(settingsComponent.getSelectedService());

    openAISettings.loadState(serviceSelectionForm.getCurrentOpenAIFormState());
    AzureSettings.getInstance().loadState(serviceSelectionForm.getCurrentAzureFormState());
    LlamaSettings.getInstance().loadState(serviceSelectionForm.getCurrentLlamaFormState());
    YouSettings.getCurrentState()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    var serviceChanged = isServiceChanged(settings);
    var modelChanged = !openAISettings.getState().getModel()
        .equals(serviceSelectionForm.getOpenAIModel());
    if (serviceChanged || modelChanged) {
      resetActiveTab();
      if (serviceChanged) {
        TelemetryAction.SETTINGS_CHANGED.createActionMessage()
            .property("service", settingsComponent.getSelectedService().getCode().toLowerCase())
            .send();
      }
    }
  }

  @Override
  public void reset() {
    var settings = GeneralSettings.getCurrentState();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    // settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());
    settingsComponent.setSelectedService(settings.getSelectedService());

    serviceSelectionForm.resetOpenAIForm();
    serviceSelectionForm.resetAzureForm();
    serviceSelectionForm.resetLlamaForm();

    serviceSelectionForm.setDisplayWebSearchResults(
        YouSettings.getCurrentState().isDisplayWebSearchResults());
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    settingsComponent = null;
  }

  private boolean isServiceChanged(GeneralSettings settings) {
    return settingsComponent.getSelectedService() != settings.getState().getSelectedService();
  }

  private void resetActiveTab() {
    ConversationsState.getInstance().setCurrentConversation(null);
    var project = ApplicationUtil.findCurrentProject();
    if (project == null) {
      throw new RuntimeException("Could not find current project.");
    }

    project.getService(StandardChatToolWindowContentManager.class).resetAll();
  }
}
