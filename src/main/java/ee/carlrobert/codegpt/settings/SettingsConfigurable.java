package ee.carlrobert.codegpt.settings;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
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
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName()) ||
        isServiceChanged(settings) ||
        openAISettings.isModified(serviceSelectionForm) ||
        azureSettings.isModified(serviceSelectionForm) ||
        serviceSelectionForm.isDisplayWebSearchResults() !=
            YouSettingsState.getInstance().isDisplayWebSearchResults() ||

        llamaSettings.isUseCustomModel() != llamaModelPreferencesForm.isUseCustomLlamaModel() ||
        llamaSettings.getServerPort() != serviceSelectionForm.getLlamaServerPort() ||
        llamaSettings.getHuggingFaceModel() != llamaModelPreferencesForm.getSelectedModel() ||
        !llamaSettings.getPromptTemplate().equals(llamaModelPreferencesForm.getPromptTemplate()) ||
        !llamaSettings.getCustomLlamaModelPath()
            .equals(llamaModelPreferencesForm.getCustomLlamaModelPath());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var llamaSettings = LlamaSettingsState.getInstance();
    var serviceChanged = isServiceChanged(settings);
    var modelChanged = openAISettings.getModel().equals(serviceSelectionForm.getOpenAIModel()) ||
        azureSettings.getModel().equals(serviceSelectionForm.getAzureModel());

    var prevKey = OpenAICredentialsManager.getInstance().getApiKey();
    if (prevKey != null && !prevKey.equals(serviceSelectionForm.getOpenAIApiKey())) {
      OpenAISettingsState.getInstance().setOpenAIQuotaExceeded(false);
    }

    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance()
        .setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    settings.setDisplayName(settingsComponent.getDisplayName());
    // TODO: Store as single enum value
    settings.setUseOpenAIService(settingsComponent.getSelectedService() == OPENAI);
    settings.setUseAzureService(settingsComponent.getSelectedService() == ServiceType.AZURE);
    settings.setUseYouService(settingsComponent.getSelectedService() == ServiceType.YOU);
    YouSettingsState.getInstance()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());
    settings.setUseLlamaService(settingsComponent.getSelectedService() == ServiceType.LLAMA_CPP);

    var llamaModelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    llamaSettings.setCustomLlamaModelPath(llamaModelPreferencesForm.getCustomLlamaModelPath());
    llamaSettings.setHuggingFaceModel(llamaModelPreferencesForm.getSelectedModel());
    llamaSettings.setUseCustomModel(llamaModelPreferencesForm.isUseCustomLlamaModel());
    llamaSettings.setPromptTemplate(llamaModelPreferencesForm.getPromptTemplate());
    llamaSettings.setServerPort(serviceSelectionForm.getLlamaServerPort());

    openAISettings.apply(serviceSelectionForm);
    azureSettings.apply(serviceSelectionForm);

    if (serviceChanged || modelChanged) {
      resetActiveTab();
      if (serviceChanged) {
        TelemetryAction.SETTINGS_CHANGED.createActionMessage()
            .property("service", getServiceCode())
            .send();
      }
    }
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var llamaSettings = LlamaSettingsState.getInstance();
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

    // settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());

    // TODO
    if (settings.isUseOpenAIService()) {
      settingsComponent.setSelectedService(OPENAI);
    }
    if (settings.isUseAzureService()) {
      settingsComponent.setSelectedService(ServiceType.AZURE);
    }
    if (settings.isUseYouService()) {
      settingsComponent.setSelectedService(ServiceType.YOU);
    }
    if (settings.isUseLlamaService()) {
      settingsComponent.setSelectedService(ServiceType.LLAMA_CPP);
    }
    var llamaModelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    llamaModelPreferencesForm.setSelectedModel(llamaSettings.getHuggingFaceModel());
    llamaModelPreferencesForm.setCustomLlamaModelPath(llamaSettings.getCustomLlamaModelPath());
    llamaModelPreferencesForm.setUseCustomLlamaModel(llamaSettings.isUseCustomModel());
    llamaModelPreferencesForm.setPromptTemplate(llamaSettings.getPromptTemplate());
    serviceSelectionForm.setLlamaServerPort(llamaSettings.getServerPort());

    openAISettings.reset(serviceSelectionForm);
    azureSettings.reset(serviceSelectionForm);

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
    return (settingsComponent.getSelectedService() == OPENAI) != settings.isUseOpenAIService() ||
        (settingsComponent.getSelectedService() == AZURE) != settings.isUseAzureService() ||
        (settingsComponent.getSelectedService() == YOU) != settings.isUseYouService() ||
        (settingsComponent.getSelectedService() == LLAMA_CPP) != settings.isUseLlamaService();
  }

  private void resetActiveTab() {
    ConversationsState.getInstance().setCurrentConversation(null);
    var project = ApplicationUtils.findCurrentProject();
    if (project == null) {
      throw new RuntimeException("Could not find current project.");
    }

    project.getService(StandardChatToolWindowContentManager.class).resetActiveTab();
  }

  private String getServiceCode() {
    if (settingsComponent.getSelectedService() == OPENAI) {
      return "openai";
    }
    if (settingsComponent.getSelectedService() == AZURE) {
      return "azure";
    }
    if (settingsComponent.getSelectedService() == YOU) {
      return "you";
    }
    if (settingsComponent.getSelectedService() == LLAMA_CPP) {
      return "llama.cpp";
    }
    return null;
  }
}
