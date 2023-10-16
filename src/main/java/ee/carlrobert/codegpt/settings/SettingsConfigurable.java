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

    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    return !settingsComponent.getDisplayName().equals(settings.getDisplayName()) ||
        isServiceChanged(serviceSelectionForm, settings) ||
        openAISettings.isModified(serviceSelectionForm) ||
        azureSettings.isModified(serviceSelectionForm) ||
        serviceSelectionForm.isDisplayWebSearchResults() !=
            YouSettingsState.getInstance().isDisplayWebSearchResults() ||
        LlamaSettingsState.getInstance().getLlamaModelPath()
            .equals(serviceSelectionForm.getLlamaModelPath());
  }

  @Override
  public void apply() {
    var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
    var settings = SettingsState.getInstance();
    var openAISettings = OpenAISettingsState.getInstance();
    var azureSettings = AzureSettingsState.getInstance();
    var llamaSettings = LlamaSettingsState.getInstance();
    var serviceChanged = isServiceChanged(serviceSelectionForm, settings);
    var modelChanged = openAISettings.getModel().equals(serviceSelectionForm.getOpenAIModel()) ||
        azureSettings.getModel().equals(serviceSelectionForm.getAzureModel());

    OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());
    AzureCredentialsManager.getInstance().setApiKey(serviceSelectionForm.getAzureOpenAIApiKey());
    AzureCredentialsManager.getInstance()
        .setAzureActiveDirectoryToken(serviceSelectionForm.getAzureActiveDirectoryToken());

    settings.setDisplayName(settingsComponent.getDisplayName());
    settings.setUseOpenAIService(serviceSelectionForm.isOpenAIServiceSelected());
    settings.setUseAzureService(serviceSelectionForm.isAzureServiceSelected());
    settings.setUseYouService(serviceSelectionForm.isYouServiceSelected());
    YouSettingsState.getInstance()
        .setDisplayWebSearchResults(serviceSelectionForm.isDisplayWebSearchResults());
    settings.setUseLlamaService(serviceSelectionForm.isLlamaServiceSelected());
    llamaSettings.setLlamaModelPath(serviceSelectionForm.getLlamaModelPath());
    llamaSettings.setLlamaModel(serviceSelectionForm.getLlamaModel());

    openAISettings.apply(serviceSelectionForm);
    azureSettings.apply(serviceSelectionForm);

    if (serviceChanged || modelChanged) {
      resetActiveTab();
      if (serviceChanged) {
        TelemetryAction.SETTINGS_CHANGED.createActionMessage()
            .property("service", getServiceCode(serviceSelectionForm))
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

    settingsComponent.setEmail(settings.getEmail());
    settingsComponent.setDisplayName(settings.getDisplayName());

    serviceSelectionForm.setOpenAIServiceSelected(settings.isUseOpenAIService());
    serviceSelectionForm.setAzureServiceSelected(settings.isUseAzureService());
    serviceSelectionForm.setYouServiceSelected(settings.isUseYouService());
    serviceSelectionForm.setLlamaServiceSelected(settings.isUseLlamaService());

    serviceSelectionForm.setLlamaModel(llamaSettings.getLlamaModel());
    serviceSelectionForm.setLlamaModelPath(llamaSettings.getLlamaModelPath());

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

  private boolean isServiceChanged(
      ServiceSelectionForm serviceSelectionForm,
      SettingsState settings) {
    return serviceSelectionForm.isOpenAIServiceSelected() != settings.isUseOpenAIService() ||
        serviceSelectionForm.isAzureServiceSelected() != settings.isUseAzureService() ||
        serviceSelectionForm.isYouServiceSelected() != settings.isUseYouService() ||
        serviceSelectionForm.isLlamaServiceSelected() != settings.isUseLlamaService();
  }

  private void resetActiveTab() {
    ConversationsState.getInstance().setCurrentConversation(null);
    var project = ApplicationUtils.findCurrentProject();
    if (project == null) {
      throw new RuntimeException("Could not find current project.");
    }

    project.getService(StandardChatToolWindowContentManager.class).resetActiveTab();
  }

  private String getServiceCode(ServiceSelectionForm serviceSelectionForm) {
    if (serviceSelectionForm.isOpenAIServiceSelected()) {
      return "openai";
    }
    if (serviceSelectionForm.isAzureServiceSelected()) {
      return "azure";
    }
    if (serviceSelectionForm.isYouServiceSelected()) {
      return "you";
    }
    if (serviceSelectionForm.isLlamaServiceSelected()) {
      return "llama.cpp";
    }
    return null;
  }
}
