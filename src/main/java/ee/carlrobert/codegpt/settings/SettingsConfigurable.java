package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.ApplicationUtils;
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
    var llamaSettings = LlamaSettingsState.getInstance();

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var llamaModelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
        || isServiceChanged(settings)
        || openAISettings.isModified(serviceSelectionForm)
        || azureSettings.isModified(serviceSelectionForm)
        || serviceSelectionForm.isDisplayWebSearchResults()
        != YouSettingsState.getInstance().isDisplayWebSearchResults()

        || llamaSettings.isUseCustomModel() != llamaModelPreferencesForm.isUseCustomLlamaModel()
        || llamaSettings.getServerPort() != serviceSelectionForm.getLlamaServerPort()
        || llamaSettings.getContextSize() != serviceSelectionForm.getContextSize()
        || llamaSettings.getHuggingFaceModel() != llamaModelPreferencesForm.getSelectedModel()
        || !llamaSettings.getPromptTemplate().equals(llamaModelPreferencesForm.getPromptTemplate())
        || !llamaSettings.getCustomLlamaModelPath()
        .equals(llamaModelPreferencesForm.getCustomLlamaModelPath());
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

    var llamaModelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    var llamaSettings = LlamaSettingsState.getInstance();
    llamaSettings.setCustomLlamaModelPath(llamaModelPreferencesForm.getCustomLlamaModelPath());
    llamaSettings.setHuggingFaceModel(llamaModelPreferencesForm.getSelectedModel());
    llamaSettings.setUseCustomModel(llamaModelPreferencesForm.isUseCustomLlamaModel());
    llamaSettings.setPromptTemplate(llamaModelPreferencesForm.getPromptTemplate());
    llamaSettings.setServerPort(serviceSelectionForm.getLlamaServerPort());
    llamaSettings.setContextSize(serviceSelectionForm.getContextSize());

    var azureSettings = AzureSettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    openAISettings.apply(serviceSelectionForm);
    azureSettings.apply(serviceSelectionForm);
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

    var llamaSettings = LlamaSettingsState.getInstance();
    var llamaModelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    llamaModelPreferencesForm.setSelectedModel(llamaSettings.getHuggingFaceModel());
    llamaModelPreferencesForm.setCustomLlamaModelPath(llamaSettings.getCustomLlamaModelPath());
    llamaModelPreferencesForm.setUseCustomLlamaModel(llamaSettings.isUseCustomModel());
    llamaModelPreferencesForm.setPromptTemplate(llamaSettings.getPromptTemplate());
    serviceSelectionForm.setLlamaServerPort(llamaSettings.getServerPort());
    serviceSelectionForm.setContextSize(llamaSettings.getContextSize());

    OpenAISettingsState.getInstance().reset(serviceSelectionForm);
    AzureSettingsState.getInstance().reset(serviceSelectionForm);

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
    var project = ApplicationUtils.findCurrentProject();
    if (project == null) {
      throw new RuntimeException("Could not find current project.");
    }

    project.getService(StandardChatToolWindowContentManager.class).resetAll();
  }
}
