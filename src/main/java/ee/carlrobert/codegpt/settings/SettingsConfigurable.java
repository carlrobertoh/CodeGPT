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
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
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
    var settings = SettingsState.getInstance();
    parentDisposable = Disposer.newDisposable();
    settingsComponent = new SettingsComponent(parentDisposable, settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettings.getInstance();
    var azureSettings = AzureSettings.getInstance();
    var llamaSettings = LlamaSettings.getInstance();

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
        || isServiceChanged(settings)
        || openAISettings.isModified(serviceSelectionForm)
        || azureSettings.isModified(serviceSelectionForm)
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettingsState.getInstance().isDisplayWebSearchResults()
        || llamaSettings.isModified(serviceSelectionForm);
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
    AzureCredentialManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialManager.getInstance()
        .setActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());
    LlamaCredentialManager.getInstance()
        .setCredential(serviceSelectionForm.getLlamaServerPreferencesForm().getApiKey());

    var settings = SettingsState.getInstance();
    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setSelectedService(settingsComponent.getSelectedService());

    var azureSettings = AzureSettings.getInstance();
    openAISettings.loadState(serviceSelectionForm.getCurrentOpenAIFormState());
    azureSettings.loadState(serviceSelectionForm.getCurrentAzureFormState());
    LlamaSettings.getInstance().loadState(serviceSelectionForm.getCurrentLlamaFormState());
    YouSettingsState.getInstance()
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
    var settings = SettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    // settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());
    settingsComponent.setSelectedService(settings.getSelectedService());

    serviceSelectionForm.resetOpenAIForm();
    serviceSelectionForm.resetAzureForm();
    serviceSelectionForm.resetLlamaForm();

    serviceSelectionForm.setDisplayWebSearchResults(
        YouSettingsState.getInstance().isDisplayWebSearchResults());
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    settingsComponent = null;
  }

  private boolean isServiceChanged(SettingsState settings) {
    return settingsComponent.getSelectedService() != settings.getSelectedService();
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
