package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.managers.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.managers.LlamaCredentialsManager;
import ee.carlrobert.codegpt.credentials.managers.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.AzureServiceForm;
import ee.carlrobert.codegpt.settings.service.LlamaServiceForm;
import ee.carlrobert.codegpt.settings.service.OpenAIServiceForm;
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
    var openAICredentialsManager = OpenAICredentialsManager.getInstance();

    var azureSettings = AzureSettings.getInstance().getState();
    var azureCredentialsManager = AzureCredentialsManager.getInstance();

    var llamaSettings = LlamaSettings.getInstance().getState();
    var llamaCredentialsManager = LlamaCredentialsManager.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    OpenAIServiceForm openAIServiceSectionPanel = serviceSelectionForm
        .getOpenAIServiceSectionPanel();
    AzureServiceForm azureServiceSectionPanel = serviceSelectionForm
        .getAzureServiceSectionPanel();

    return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
        || isServiceChanged(settings)
        || openAISettings.isModified(openAIServiceSectionPanel.getSettings())
        || openAICredentialsManager.getCredentials()
        .isModified(openAIServiceSectionPanel.getCredentials())
        || azureSettings.isModified(azureServiceSectionPanel.getSettings())
        || azureCredentialsManager.getCredentials()
        .isModified(azureServiceSectionPanel.getCredentials())
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettings.getInstance().getState().isDisplayWebSearchResults()
        || llamaSettings.isModified(
        serviceSelectionForm.getLlamaServiceSectionPanel().getSettings())
        || llamaCredentialsManager.getCredentials().isModified(
        serviceSelectionForm.getLlamaServiceSectionPanel().getRemoteCredentials());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    OpenAICredentialsManager openAICredentialsManager = OpenAICredentialsManager.getInstance();
    var openAICredentials = openAICredentialsManager.getCredentials();
    var prevKey = openAICredentials.getApiKey();
    OpenAIServiceForm openAIServiceSectionPanel = serviceSelectionForm
        .getOpenAIServiceSectionPanel();
    if (prevKey != null && !prevKey.equals(
        openAIServiceSectionPanel.getCredentials().getApiKey())) {
      OpenAISettings.getInstance().getState().setOpenAIQuotaExceeded(false);
    }

    var settings = GeneralSettingsState.getInstance();
    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setSelectedService(settingsComponent.getSelectedService());

    var openAISettings = OpenAISettings.getInstance();
    openAISettings.apply(openAIServiceSectionPanel.getSettings());
    openAICredentialsManager.apply(openAIServiceSectionPanel.getCredentials());

    AzureServiceForm azureServiceSectionPanel = serviceSelectionForm.getAzureServiceSectionPanel();
    AzureSettings.getInstance().apply(azureServiceSectionPanel.getSettings());
    AzureCredentialsManager.getInstance().apply(azureServiceSectionPanel.getCredentials());

    LlamaServiceForm llamaServiceSectionPanel = serviceSelectionForm.getLlamaServiceSectionPanel();
    LlamaSettings.getInstance().apply(llamaServiceSectionPanel.getSettings());
    LlamaCredentialsManager.getInstance().apply(llamaServiceSectionPanel.getRemoteCredentials());

    YouSettings.getInstance().getState()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());

    var serviceChanged = isServiceChanged(settings);
    var modelChanged = !openAISettings.getState().getModel()
        .equals(openAIServiceSectionPanel.getSettings().getModel());
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

    OpenAIServiceForm openAIServiceSectionPanel = serviceSelectionForm
        .getOpenAIServiceSectionPanel();
    openAIServiceSectionPanel.setSettings(OpenAISettings.getInstance().getState());
    openAIServiceSectionPanel.setCredentials(
        OpenAICredentialsManager.getInstance().getCredentials());

    AzureServiceForm azureServiceSectionPanel = serviceSelectionForm.getAzureServiceSectionPanel();
    azureServiceSectionPanel.setSettings(AzureSettings.getInstance().getState());
    azureServiceSectionPanel.setCredentials(AzureCredentialsManager.getInstance().getCredentials());

    LlamaServiceForm llamaServiceSectionPanel = serviceSelectionForm.getLlamaServiceSectionPanel();
    llamaServiceSectionPanel.setSettings(LlamaSettings.getInstance().getState());
    llamaServiceSectionPanel.setRemoteCredentials(
        LlamaCredentialsManager.getInstance().getCredentials());

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
