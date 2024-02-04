package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.LlamaServiceForm;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;

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
        || localSettings.getCredentialsManager()
        .isModified(settingsState.getLocalSettings().getCredentialsManager().getApiKey())
        || remoteSettings.isModified(settingsState.getRemoteSettings())
        || remoteSettings.getCredentialsManager()
        .isModified(settingsState.getRemoteSettings().getCredentialsManager().getApiKey())
        || runLocalServer != settingsState.isRunLocalServer()
        || requestSettings.isModified(settingsState.getRequestSettings());
  }

  public void apply(LlamaSettingsState settingsState) {
    runLocalServer = settingsState.isRunLocalServer();

    LlamaCredentialsManager localCredentials = applyCredentials(localSettings,
        settingsState.getLocalSettings());
    localSettings = settingsState.getLocalSettings();
    localSettings.setCredentialsManager(localCredentials);

    LlamaCredentialsManager remoteCredentials = applyCredentials(remoteSettings,
        settingsState.getRemoteSettings());
    remoteSettings = settingsState.getRemoteSettings();
    remoteSettings.setCredentialsManager(remoteCredentials);

    requestSettings = settingsState.getRequestSettings();
  }

  private LlamaCredentialsManager applyCredentials(
      CommonSettings<LlamaCredentialsManager> to, CommonSettings<LlamaCredentialsManager> from) {
    to.getCredentialsManager().apply(from.getCredentialsManager().getApiKey());
    return to.getCredentialsManager();
  }


  public void reset(LlamaServiceForm llamaServiceForm) {
    var llamaServerForm = llamaServiceForm.getServerPreferencesForm();
    llamaServerForm.setRemoteSettings(remoteSettings);
    llamaServerForm.setLocalSettings(localSettings);
    llamaServerForm.setRunLocalServer(runLocalServer);
    var llamaRequestsForm = llamaServiceForm.getRequestPreferencesForm();
    llamaRequestsForm.setTopK(requestSettings.getTopK());
    llamaRequestsForm.setTopP(requestSettings.getTopP());
    llamaRequestsForm.setMinP(requestSettings.getMinP());
    llamaRequestsForm.setRepeatPenalty(requestSettings.getRepeatPenalty());
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

}
