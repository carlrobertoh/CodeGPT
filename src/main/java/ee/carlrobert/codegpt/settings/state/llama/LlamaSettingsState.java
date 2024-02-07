package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;

public class LlamaSettingsState {

  protected boolean runLocalServer = true;
  protected LlamaLocalSettings localSettings = new LlamaLocalSettings();
  protected LlamaRemoteSettings remoteSettings = new LlamaRemoteSettings();
  protected LlamaRequestSettings requestSettings = new LlamaRequestSettings();

  public LlamaSettingsState() {
  }

  public LlamaSettingsState(boolean runLocalServer, LlamaLocalSettings localSettings,
      LlamaRemoteSettings remoteSettings, LlamaRequestSettings requestSettings) {
    this.runLocalServer = runLocalServer;
    this.localSettings = localSettings;
    this.remoteSettings = remoteSettings;
    this.requestSettings = requestSettings;
  }

  public String getUsedModelPath() {
    return LlamaCompletionModel.getModelPath(localSettings.getModel());
  }

  public LlamaCompletionModel getUsedModel() {
    return localSettings.getModel();
  }

  public boolean isRunLocalServer() {
    return runLocalServer;
  }

  public void setRunLocalServer(boolean runLocalServer) {
    this.runLocalServer = runLocalServer;
  }

  @Transient
  public boolean isModified(LlamaSettingsState settingsState) {
    return localSettings.isModified(settingsState.getLocalSettings())
        || remoteSettings.isModified(settingsState.getRemoteSettings())
        || runLocalServer != settingsState.isRunLocalServer()
        || requestSettings.isModified(settingsState.getRequestSettings());
  }


  public LlamaLocalSettings getLocalSettings() {
    return localSettings;
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    this.localSettings = localSettings;
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return remoteSettings;
  }

  public void setRemoteSettings(LlamaRemoteSettings remoteSettings) {
    this.remoteSettings = remoteSettings;
  }

  public LlamaRequestSettings getRequestSettings() {
    return requestSettings;
  }

  public void setRequestSettings(
      LlamaRequestSettings llamaRequestSettings) {
    this.requestSettings = llamaRequestSettings;
  }

  @Transient
  public PromptTemplate getUsedPromptTemplate() {
    if (isRunLocalServer()) {
      var localSettings = getLocalSettings();
      LlamaCompletionModel model = localSettings.getModel();
      if (model instanceof HuggingFaceModel) {
        return LlamaModel.findByHuggingFaceModel((HuggingFaceModel) model)
            .getPromptTemplate();
      }
      return localSettings.getChatPromptTemplate();

    } else {
      return getRemoteSettings().getChatPromptTemplate();
    }
  }

}
