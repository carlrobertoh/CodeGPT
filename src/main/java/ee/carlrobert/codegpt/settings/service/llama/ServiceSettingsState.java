package ee.carlrobert.codegpt.settings.service.llama;

import ee.carlrobert.codegpt.settings.state.llama.LocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.RemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.RequestSettings;

public class ServiceSettingsState {

  protected boolean runLocalServer = true;
  protected LocalSettings localSettings = new LocalSettings();
  protected RemoteSettings remoteSettings = new RemoteSettings();
  protected RequestSettings requestSettings = new RequestSettings();

  public boolean isRunLocalServer() {
    return runLocalServer;
  }

  public void setRunLocalServer(boolean runLocalServer) {
    this.runLocalServer = runLocalServer;
  }

  public boolean isModified(ServerPreferencesForm serverPreferencesForm ,RequestPreferencesForm requestPreferencesForm) {
    return localSettings.isModified(serverPreferencesForm.getLocalSettings(),
        serverPreferencesForm.getLocalApiKey())
        || remoteSettings.isModified(serverPreferencesForm.getRemoteSettings(),
        serverPreferencesForm.getRemoteApikey())
        || runLocalServer != serverPreferencesForm.isRunLocalServer()
        || requestSettings.isModified(requestPreferencesForm.getRequestSettings());
  }

  public void apply(ServerPreferencesForm serverPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    runLocalServer = serverPreferencesForm.isRunLocalServer();
    localSettings = serverPreferencesForm.getLocalSettings();
    localSettings.getCredentialsManager().apply(serverPreferencesForm.getLocalApiKey());
    remoteSettings = serverPreferencesForm.getRemoteSettings();
    remoteSettings.getCredentialsManager().apply(serverPreferencesForm.getRemoteApikey());
    requestSettings = requestPreferencesForm.getRequestSettings();
  }

  public void reset(ServerPreferencesForm serverPreferencesForm,
      RequestPreferencesForm requestPreferencesForm) {
    serverPreferencesForm.setRemoteSettings(remoteSettings);
    serverPreferencesForm.setLocalSettings(localSettings);
    serverPreferencesForm.setRunLocalServer(runLocalServer);
    requestPreferencesForm.setTopK(requestSettings.getTopK());
    requestPreferencesForm.setTopP(requestSettings.getTopP());
    requestPreferencesForm.setMinP(requestSettings.getMinP());
    requestPreferencesForm.setRepeatPenalty(requestSettings.getRepeatPenalty());
  }

  public LocalSettings getLocalSettings() {
    return localSettings;
  }

  public void setLocalSettings(LocalSettings localSettings) {
    this.localSettings = localSettings;
  }

  public RemoteSettings getRemoteSettings() {
    return remoteSettings;
  }

  public void setRemoteSettings(RemoteSettings remoteSettings) {
    this.remoteSettings = remoteSettings;
  }

  public RequestSettings getRequestSettings() {
    return requestSettings;
  }

  public void setRequestSettings(
      RequestSettings requestSettings) {
    this.requestSettings = requestSettings;
  }
}
