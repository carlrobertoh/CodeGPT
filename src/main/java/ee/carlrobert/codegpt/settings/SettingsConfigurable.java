package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.service.AzureServiceForm;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
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
    var settings = GeneralSettingsState.getInstance();
    parentDisposable = Disposer.newDisposable();
    settingsComponent = new SettingsComponent(parentDisposable, settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = GeneralSettingsState.getInstance();
    var openAISettings = OpenAISettings.getInstance().getState();
    var azureSettings = AzureSettings.getInstance().getState();
    var llamaSettings = LlamaSettings.getInstance().getState();

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
        || isServiceChanged(settings)
        || openAISettings.isModified(serviceSelectionForm.getOpenAIServiceSectionPanel()
        .getSettings())
        || azureSettings.isModified(
        serviceSelectionForm.getAzureServiceSectionPanel().getSettings())
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettings.getInstance().getState().isDisplayWebSearchResults()
        || llamaSettings.isModified(
        serviceSelectionForm.getLlamaServiceSectionPanel().getSettings());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var credentialsManager = OpenAISettings.getInstance().getState()
        .getCredentials();
    var prevKey = credentialsManager.getApiKey();
    if (prevKey != null && !prevKey.equals(
        serviceSelectionForm.getOpenAIServiceSectionPanel().getApiKey())) {
      OpenAISettings.getInstance().getState().setOpenAIQuotaExceeded(false);
    }

    var settings = GeneralSettingsState.getInstance();
    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setSelectedService(settingsComponent.getSelectedService());

    var openAISettings = OpenAISettings.getInstance();
    openAISettings.apply(serviceSelectionForm.getOpenAIServiceSectionPanel().getSettings());

    AzureServiceForm azureServiceSectionPanel = serviceSelectionForm.getAzureServiceSectionPanel();
    AzureSettings.getInstance().apply(azureServiceSectionPanel.getSettings());

    LlamaSettings.getInstance()
        .apply(serviceSelectionForm.getLlamaServiceSectionPanel().getSettings());
    YouSettings.getInstance().getState()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    var serviceChanged = isServiceChanged(settings);
    var modelChanged = !openAISettings.getState().getModel()
        .equals(serviceSelectionForm.getOpenAIServiceSectionPanel().getModel());
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
    var settings = GeneralSettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    // settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());
    settingsComponent.setSelectedService(settings.getSelectedService());

    serviceSelectionForm.getOpenAIServiceSectionPanel()
        .setSettings(OpenAISettings.getInstance().getState());
    serviceSelectionForm.getAzureServiceSectionPanel()
        .setSettings(AzureSettings.getInstance().getState());
    serviceSelectionForm.getLlamaServiceSectionPanel()
        .setSettings(LlamaSettings.getInstance().getState());
    serviceSelectionForm.setDisplayWebSearchResults(
        YouSettings.getInstance().getState().isDisplayWebSearchResults());
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    settingsComponent = null;
  }

  private boolean isServiceChanged(GeneralSettingsState settings) {
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
