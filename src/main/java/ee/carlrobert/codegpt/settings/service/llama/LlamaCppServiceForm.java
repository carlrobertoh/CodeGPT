package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.LlamaServiceForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.LlamaCppSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;

public class LlamaCppServiceForm extends LlamaServiceForm<LlamaRemoteSettings> {

  public LlamaCppServiceForm(LlamaCppSettingsState settingsState) {
    super(new LlamaLocalOrRemoteServiceForm<>(ServiceType.LLAMA_CPP, settingsState,
            ApplicationManager.getApplication().getService(LlamaServerAgent.class),
            new LlamaRemoteServiceForm<>(settingsState.getRemoteSettings(), ServiceType.LLAMA_CPP) {
              @Override
              public LlamaRemoteSettings getRemoteSettings() {
                return new LlamaRemoteSettings(
                    chatPromptTemplatePanel.getPromptTemplate(),
                    infillPromptTemplatePanel.getPromptTemplate(),
                    baseHostField.getText(),
                    new LlamaCredentialsManager(LlamaRemoteSettings.CREDENTIALS_PREFIX) {
                      @Override
                      public String getApiKey() {
                        return new String(apiKeyField.getPassword());
                      }
                    }
                );
              }
            }),
        settingsState.getRequestSettings());
  }

  @Override
  public LlamaCppSettingsState getSettings() {
    LlamaSettingsState<LlamaRemoteSettings> settings = super.getSettings();
    return new LlamaCppSettingsState(settings.isRunLocalServer(),
        settings.getLocalSettings(), settings.getRemoteSettings(), settings.getRequestSettings());
  }
}
