package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.OllamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import ee.carlrobert.codegpt.settings.state.llama.ollama.OllamaRemoteSettings;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaTagsResponse;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

//@State(name = "CodeGPT_OllamaSettings", storages = @Storage("CodeGPT_CodeGPT_OllamaSettings.xml"))
public class OllamaSettingsState extends LlamaSettingsState<OllamaRemoteSettings> implements
    PersistentStateComponent<OllamaSettingsState> {

  public OllamaSettingsState() {
    super(new OllamaRemoteSettings());
  }

  public OllamaSettingsState(boolean runLocalServer, LlamaLocalSettings localSettings,
      OllamaRemoteSettings remoteSettings,
      LlamaRequestSettings requestSettings) {
    super(runLocalServer, localSettings, remoteSettings, requestSettings);
  }

  @Override
  public OllamaSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OllamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static OllamaSettingsState getInstance() {
    OllamaSettingsState service = ApplicationManager.getApplication()
        .getService(OllamaSettingsState.class);
    service.localSettings.setCredentialsManager(
        new OllamaCredentialsManager(LlamaLocalSettings.CREDENTIALS_PREFIX));
    service.remoteSettings.setCredentialsManager(
        new OllamaCredentialsManager(LlamaRemoteSettings.CREDENTIALS_PREFIX));
    return service;
  }

  @Transient
  public boolean isModified(OllamaSettingsState settingsState) {
    return localSettings.isModified(settingsState.getLocalSettings())
        || remoteSettings.isModified(settingsState.getRemoteSettings())
        || runLocalServer != settingsState.isRunLocalServer()
        || requestSettings.isModified(settingsState.getRequestSettings());
  }

  public void apply(OllamaSettingsState settingsState) {
    runLocalServer = settingsState.isRunLocalServer();
    localSettings = settingsState.getLocalSettings();
    remoteSettings = settingsState.getRemoteSettings();
    requestSettings = settingsState.getRequestSettings();
  }

  @Transient
  private List<OllamaModel> availableOllamaModels = null;

  @Transient
  public List<OllamaModel> getAvailableModels() {
    if (availableOllamaModels == null) {
      OllamaTagsResponse modelTags = CompletionClientProvider.getOllamaClient().getModelTags();
      availableOllamaModels = new ArrayList<>(modelTags.getModels());
    }
    return availableOllamaModels;
  }

  @Override
  public LlamaCompletionModel getUsedModel() {
    return isRunLocalServer() ? localSettings.getModel() : remoteSettings.getModel();
  }

  @Transient
  public void addModelToAvailable(OllamaModel model) {
    availableOllamaModels.add(model);
  }


}
