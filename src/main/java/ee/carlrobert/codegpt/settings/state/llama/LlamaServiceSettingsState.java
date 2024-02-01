package ee.carlrobert.codegpt.settings.state.llama;

import ee.carlrobert.codegpt.settings.service.llama.RequestPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaServerPreferencesForm;

public class LlamaServiceSettingsState {

  protected boolean runLocalServer = true;
  protected LlamaLocalSettings localSettings = new LlamaLocalSettings();
  protected LlamaRemoteSettings remoteSettings = new LlamaRemoteSettings();
  protected LlamaRequestSettings llamaRequestSettings = new LlamaRequestSettings();

  public boolean isRunLocalServer() {
    return runLocalServer;
  }

  public void setRunLocalServer(boolean runLocalServer) {
    this.runLocalServer = runLocalServer;
  }

  public boolean isModified(LlamaServerPreferencesForm llamaServerPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    return localSettings.isModified(llamaServerPreferencesForm.getLocalSettings())
        || localSettings.getCredentialsManager().isModified(llamaServerPreferencesForm.getLocalApiKey())
        || remoteSettings.isModified(llamaServerPreferencesForm.getRemoteSettings())
        || remoteSettings.getCredentialsManager()
        .isModified(llamaServerPreferencesForm.getRemoteApikey())
        || runLocalServer != llamaServerPreferencesForm.isRunLocalServer()
        || llamaRequestSettings.isModified(requestPreferencesForm.getRequestSettings());
  }

  public void apply(LlamaServerPreferencesForm llamaServerPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    runLocalServer = llamaServerPreferencesForm.isRunLocalServer();
    localSettings = llamaServerPreferencesForm.getLocalSettings();
    localSettings.getCredentialsManager().apply(llamaServerPreferencesForm.getLocalApiKey());
    remoteSettings = llamaServerPreferencesForm.getRemoteSettings();
    remoteSettings.getCredentialsManager().apply(llamaServerPreferencesForm.getRemoteApikey());
    llamaRequestSettings = requestPreferencesForm.getRequestSettings();
  }

  public void reset(LlamaServerPreferencesForm llamaServerPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    llamaServerPreferencesForm.setRemoteSettings(remoteSettings);
    llamaServerPreferencesForm.setLocalSettings(localSettings);
    llamaServerPreferencesForm.setRunLocalServer(runLocalServer);
    requestPreferencesForm.setTopK(llamaRequestSettings.getTopK());
    requestPreferencesForm.setTopP(llamaRequestSettings.getTopP());
    requestPreferencesForm.setMinP(llamaRequestSettings.getMinP());
    requestPreferencesForm.setRepeatPenalty(llamaRequestSettings.getRepeatPenalty());
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
    return llamaRequestSettings;
  }

  public void setRequestSettings(
      LlamaRequestSettings llamaRequestSettings) {
    this.llamaRequestSettings = llamaRequestSettings;
  }
}
