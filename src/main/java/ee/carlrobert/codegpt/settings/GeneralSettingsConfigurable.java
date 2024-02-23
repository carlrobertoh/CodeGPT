package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.CustomServiceCredentialManager;
import ee.carlrobert.codegpt.credentials.LlamaCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceForm;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm;
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
    var settings = GeneralSettings.getCurrentState();
    var serviceSelectionForm = component.getServiceSelectionForm();
    return !component.getDisplayName().equals(settings.getDisplayName())
        || component.getSelectedService() != settings.getSelectedService()
        || OpenAISettings.getInstance().isModified(serviceSelectionForm.getOpenAISettingsForm())
        || CustomServiceSettings.getInstance()
        .isModified(serviceSelectionForm.getCustomConfigurationSettingsForm())
        || AzureSettings.getInstance().isModified(serviceSelectionForm.getAzureSettingsForm())
        || YouSettings.getInstance().isModified(serviceSelectionForm.getYouSettingsForm())
        || LlamaSettings.getInstance().isModified(serviceSelectionForm.getLlamaSettingsForm());
  }

  @Override
  public void apply() {
    var settings = GeneralSettings.getCurrentState();
    settings.setDisplayName(component.getDisplayName());
    settings.setSelectedService(component.getSelectedService());

    var serviceSelectionForm = component.getServiceSelectionForm();
    var openAISettingsForm = serviceSelectionForm.getOpenAISettingsForm();
    applyOpenAISettings(openAISettingsForm);
    applyCustomOpenAISettings(serviceSelectionForm.getCustomConfigurationSettingsForm());
    applyAzureSettings(serviceSelectionForm.getAzureSettingsForm());
    applyYouSettings(serviceSelectionForm.getYouSettingsForm());
    applyLlamaSettings(serviceSelectionForm.getLlamaSettingsForm());

    var serviceChanged = component.getSelectedService() != settings.getSelectedService();
    var modelChanged = !OpenAISettings.getCurrentState().getModel()
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

  private void applyOpenAISettings(OpenAISettingsForm form) {
    OpenAICredentialManager.getInstance().setCredential(form.getApiKey());
    OpenAISettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyCustomOpenAISettings(CustomServiceForm form) {
    CustomServiceCredentialManager.getInstance().setCredential(form.getApiKey());
    CustomServiceSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyLlamaSettings(LlamaSettingsForm form) {
    LlamaCredentialManager.getInstance()
        .setCredential(form.getLlamaServerPreferencesForm().getApiKey());
    LlamaSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyYouSettings(YouSettingsForm form) {
    YouSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyAzureSettings(AzureSettingsForm form) {
    AzureSettings.getInstance().loadState(form.getCurrentState());
    var azureCredentials = AzureCredentialsManager.getInstance();
    azureCredentials.setApiKey(form.getApiKey());
    azureCredentials.setActiveDirectoryToken(form.getActiveDirectoryToken());
  }

  @Override
  public void reset() {
    var settings = GeneralSettings.getCurrentState();
    component.setDisplayName(settings.getDisplayName());
    component.setSelectedService(settings.getSelectedService());
    component.getServiceSelectionForm().resetForms();
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    component = null;
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
