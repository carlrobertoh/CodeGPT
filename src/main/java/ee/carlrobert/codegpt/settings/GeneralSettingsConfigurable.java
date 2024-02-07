package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialManager;
import ee.carlrobert.codegpt.credentials.LlamaCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.ApplicationUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class GeneralSettingsConfigurable implements Configurable {

  private Disposable parentDisposable;

  private GeneralSettingsComponent component;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("settings.displayName");
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return component.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var settings = GeneralSettings.getInstance();
    parentDisposable = Disposer.newDisposable();
    component = new GeneralSettingsComponent(parentDisposable, settings);
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = GeneralSettings.getInstance();
    var serviceSelectionForm = component.getServiceSelectionForm();
    return !component.getDisplayName().equals(settings.getState().getDisplayName())
        || isServiceChanged(settings)
        || OpenAISettings.getInstance().isModified(serviceSelectionForm.getOpenAISettingsForm())
        || AzureSettings.getInstance().isModified(serviceSelectionForm.getAzureSettingsForm())
        || YouSettings.getInstance().isModified(serviceSelectionForm.getYouSettingsForm())
        || LlamaSettings.getInstance().isModified(serviceSelectionForm.getLlamaSettingsForm());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = component.getServiceSelectionForm();
    var openAISettingsForm = serviceSelectionForm.getOpenAISettingsForm();
    var openAISettings = OpenAISettings.getInstance();

    var prevKey = OpenAICredentialManager.getInstance().getCredential();
    if (prevKey != null && !prevKey.equals(openAISettingsForm.getApiKey())) {
      openAISettings.getState().setOpenAIQuotaExceeded(false);
    }

    OpenAICredentialManager.getInstance().setCredential(openAISettingsForm.getApiKey());
    var azureCredentials = AzureCredentialManager.getInstance();
    azureCredentials.setApiKey(serviceSelectionForm.getAzureSettingsForm().getApiKey());
    azureCredentials.setActiveDirectoryToken(
        serviceSelectionForm.getAzureSettingsForm().getActiveDirectoryToken());
    LlamaCredentialManager.getInstance()
        .setCredential(serviceSelectionForm.getLlamaSettingsForm().getLlamaServerPreferencesForm()
            .getApiKey());

    var settings = GeneralSettings.getInstance();
    settings.getState().setDisplayName(component.getDisplayName());
    settings.getState().setSelectedService(component.getSelectedService());

    openAISettings.loadState(openAISettings.getState());
    AzureSettings.getInstance()
        .loadState(serviceSelectionForm.getAzureSettingsForm().getCurrentFormState());
    LlamaSettings.getInstance()
        .loadState(serviceSelectionForm.getLlamaSettingsForm().getCurrentFormState());
    YouSettings.getCurrentState().setDisplayWebSearchResults(
        serviceSelectionForm.getYouSettingsForm().isDisplayWebSearchResults());

    var serviceChanged = isServiceChanged(settings);
    var modelChanged = !openAISettings.getState().getModel()
        .equals(openAISettingsForm.getModel());
    if (serviceChanged || modelChanged) {
      resetActiveTab();
      if (serviceChanged) {
        TelemetryAction.SETTINGS_CHANGED.createActionMessage()
            .property("service", component.getSelectedService().getCode().toLowerCase())
            .send();
      }
    }
  }

  @Override
  public void reset() {
    var settings = GeneralSettings.getCurrentState();
    var serviceSelectionForm = component.getServiceSelectionForm();

    // settingsComponent.setEmail(settings.getEmail());
    component.setDisplayName(settings.getDisplayName());
    component.setSelectedService(settings.getSelectedService());

    serviceSelectionForm.getOpenAISettingsForm().resetForm();
    serviceSelectionForm.getAzureSettingsForm().resetForm();
    serviceSelectionForm.getLlamaSettingsForm().resetForm();
    serviceSelectionForm.getYouSettingsForm().resetForm();
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    component = null;
  }

  private boolean isServiceChanged(GeneralSettings settings) {
    return component.getSelectedService() != settings.getState().getSelectedService();
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
