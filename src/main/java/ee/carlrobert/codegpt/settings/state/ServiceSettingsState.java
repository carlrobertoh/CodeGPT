package ee.carlrobert.codegpt.settings.state;

import ee.carlrobert.codegpt.settings.service.llama.RequestPreferencesForm;
import ee.carlrobert.codegpt.settings.service.llama.ServerPreferencesForm;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;

public class ServiceSettingsState {

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

  public boolean isModified(ServerPreferencesForm serverPreferencesForm ,
      RequestPreferencesForm requestPreferencesForm) {
    return localSettings.isModified(serverPreferencesForm.getLocalSettings(),
        serverPreferencesForm.getLocalApiKey())
        || remoteSettings.isModified(serverPreferencesForm.getRemoteSettings(),
        serverPreferencesForm.getRemoteApikey())
        || runLocalServer != serverPreferencesForm.isRunLocalServer()
        || llamaRequestSettings.isModified(requestPreferencesForm.getRequestSettings());
  }

  public void apply(ServerPreferencesForm serverPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    runLocalServer = serverPreferencesForm.isRunLocalServer();
    localSettings = serverPreferencesForm.getLocalSettings();
    localSettings.getCredentialsManager().apply(serverPreferencesForm.getLocalApiKey());
    remoteSettings = serverPreferencesForm.getRemoteSettings();
    remoteSettings.getCredentialsManager().apply(serverPreferencesForm.getRemoteApikey());
    llamaRequestSettings = requestPreferencesForm.getRequestSettings();
  }

  public void reset(ServerPreferencesForm serverPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    serverPreferencesForm.setRemoteSettings(remoteSettings);
    serverPreferencesForm.setLocalSettings(localSettings);
    serverPreferencesForm.setRunLocalServer(runLocalServer);
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
