package ee.carlrobert.codegpt.settings;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.ANTHROPIC_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CODEGPT_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.GOOGLE_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.LLAMA_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettingsForm;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceForm;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceForm;
import ee.carlrobert.codegpt.settings.service.google.GoogleSettingsForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager;
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

    return !component.getDisplayName().equals(settings.getDisplayName())
        || component.getSelectedService() != settings.getSelectedService()
        || component.getCodeGPTSettingsForm().isModified()
        || OpenAISettings.getInstance().isModified(component.getOpenAISettingsForm())
        || component.getCustomConfigurationSettingsForm().isModified()
        || AnthropicSettings.getInstance().isModified(component.getAnthropicSettingsForm())
        || AzureSettings.getInstance().isModified(component.getAzureSettingsForm())
        || YouSettings.getInstance().isModified(component.getYouSettingsForm())
        || LlamaSettings.getInstance().isModified(component.getLlamaSettingsForm())
        || component.getOllamaSettingsForm().isModified()
        || component.getGoogleSettingsForm().isModified();
  }

  @Override
  public void apply() {
    var settings = GeneralSettings.getCurrentState();
    settings.setDisplayName(component.getDisplayName());
    settings.setSelectedService(component.getSelectedService());

    applyCodeGPTServiceSettings(component.getCodeGPTSettingsForm());
    var openAISettingsForm = component.getOpenAISettingsForm();
    applyOpenAISettings(openAISettingsForm);
    applyCustomOpenAISettings(component.getCustomConfigurationSettingsForm());
    applyAnthropicSettings(component.getAnthropicSettingsForm());
    applyAzureSettings(component.getAzureSettingsForm());
    applyYouSettings(component.getYouSettingsForm());
    applyLlamaSettings(component.getLlamaSettingsForm());
    component.getOllamaSettingsForm().applyChanges();
    applyGoogleSettings(component.getGoogleSettingsForm());

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

  private void applyCodeGPTServiceSettings(CodeGPTServiceForm form) {
    CredentialsStore.INSTANCE.setCredential(CODEGPT_API_KEY, form.getApiKey());
    form.applyChanges();
  }

  private void applyOpenAISettings(OpenAISettingsForm form) {
    CredentialsStore.INSTANCE.setCredential(OPENAI_API_KEY, form.getApiKey());
    OpenAISettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyCustomOpenAISettings(CustomServiceForm form) {
    CredentialsStore.INSTANCE.setCredential(CUSTOM_SERVICE_API_KEY, form.getApiKey());
    form.applyChanges();
  }

  private void applyLlamaSettings(LlamaSettingsForm form) {
    CredentialsStore.INSTANCE.setCredential(
        LLAMA_API_KEY,
        form.getLlamaServerPreferencesForm().getApiKey());

    LlamaSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyYouSettings(YouSettingsForm form) {
    YouSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyAnthropicSettings(AnthropicSettingsForm form) {
    CredentialsStore.INSTANCE.setCredential(ANTHROPIC_API_KEY, form.getApiKey());
    AnthropicSettings.getInstance().loadState(form.getCurrentState());
  }

  private void applyAzureSettings(AzureSettingsForm form) {
    AzureSettings.getInstance().loadState(form.getCurrentState());
    CredentialsStore.INSTANCE.setCredential(AZURE_OPENAI_API_KEY, form.getApiKey());
    CredentialsStore.INSTANCE.setCredential(
        AZURE_ACTIVE_DIRECTORY_TOKEN,
        form.getActiveDirectoryToken());
  }

  private void applyGoogleSettings(GoogleSettingsForm form) {
    form.applyChanges();
    CredentialsStore.INSTANCE.setCredential(GOOGLE_API_KEY, form.getApiKey());
  }

  @Override
  public void reset() {
    var settings = GeneralSettings.getCurrentState();
    component.setDisplayName(settings.getDisplayName());
    component.setSelectedService(settings.getSelectedService());
    component.resetForms();
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

    project.getService(ChatToolWindowContentManager.class).resetAll();
  }
}
