package ee.carlrobert.codegpt.settings.service.llama.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.completions.ollama.OllamaServerAgent;
import ee.carlrobert.codegpt.credentials.OllamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.LlamaServiceForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.llama.LlamaLocalOrRemoteServiceForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaModelSelector;
import ee.carlrobert.codegpt.settings.service.llama.LlamaRemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OllamaSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.ollama.OllamaRemoteSettings;
import java.util.List;

public class OllamaServiceForm extends LlamaServiceForm<OllamaRemoteSettings> {

  public OllamaServiceForm(OllamaSettingsState settingsState) {
    super(new LlamaLocalOrRemoteServiceForm<>(ServiceType.OLLAMA, settingsState,
        ApplicationManager.getApplication().getService(OllamaServerAgent.class),
        new LlamaRemoteServiceForm<>(settingsState.getRemoteSettings(), ServiceType.OLLAMA) {
          private LlamaModelSelector modelSelector;

          @Override
          protected List<PanelBuilder> additionalServerConfigPanels() {
            modelSelector = new LlamaModelSelector(serviceType, remoteSettings.getModel(),
                remoteSettings.getChatPromptTemplate(),
                remoteSettings.getInfillPromptTemplate());
            return List.of(UI.PanelFactory.panel(modelSelector.getComponent()));
          }

          @Override
          public void setRemoteSettings(OllamaRemoteSettings settings) {
            super.setRemoteSettings(settings);
            modelSelector.setSelectedModel(settings.getModel());
          }

          @Override
          public OllamaRemoteSettings getRemoteSettings() {
            return new OllamaRemoteSettings(
                modelSelector.getChatPromptTemplate(),
                modelSelector.getInfillPromptTemplate(),
                baseHostField.getText(),
                new OllamaCredentialsManager(LlamaRemoteSettings.CREDENTIALS_PREFIX),
                modelSelector.getSelectedModel()
            );
          }

        }), settingsState.getRequestSettings());
  }

  @Override
  public OllamaSettingsState getSettings() {
    LlamaSettingsState<OllamaRemoteSettings> settings = super.getSettings();
    return new OllamaSettingsState(settings.isRunLocalServer(), settings.getLocalSettings(),
        settings.getRemoteSettings(), settings.getRequestSettings());
  }
}
