package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.cpp.LlamaCppSettingsState;
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
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var llamaSettings = LlamaCppSettingsState.getInstance();

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
        || isServiceChanged(settings)
        || openAISettings.isModified(serviceSelectionForm)
        || azureSettings.isModified(serviceSelectionForm)
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettingsState.getInstance().isDisplayWebSearchResults()
        || llamaSettings.isModified(serviceSelectionForm.getLlamaServerPreferencesForm(),
        serviceSelectionForm.getLlamaRequestPreferencesForm());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    var prevKey = OpenAICredentialsManager.getInstance().getApiKey();
    if (prevKey != null && !prevKey.equals(serviceSelectionForm.getOpenAIApiKey())) {
      OpenAISettingsState.getInstance().setOpenAIQuotaExceeded(false);
    }

    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance()
        .setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    var settings = SettingsState.getInstance();
    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setSelectedService(settingsComponent.getSelectedService());

    var azureSettings = AzureSettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    openAISettings.apply(serviceSelectionForm);
    azureSettings.apply(serviceSelectionForm);
    LlamaCppSettingsState.getInstance().apply(serviceSelectionForm.getLlamaServerPreferencesForm(),
        serviceSelectionForm.getLlamaRequestPreferencesForm());
    YouSettingsState.getInstance()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    var serviceChanged = isServiceChanged(settings);
    var modelChanged = !openAISettings.getModel().equals(serviceSelectionForm.getOpenAIModel());
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

    OpenAISettingsState.getInstance().reset(serviceSelectionForm);
    AzureSettingsState.getInstance().reset(serviceSelectionForm);
    LlamaCppSettingsState.getInstance().reset(serviceSelectionForm.getLlamaServerPreferencesForm(),
        serviceSelectionForm.getLlamaRequestPreferencesForm());

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
