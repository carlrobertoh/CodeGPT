package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.LlamaServiceForm;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  protected boolean runLocalServer = true;
  protected LlamaLocalSettings localSettings = new LlamaLocalSettings();
  protected LlamaRemoteSettings remoteSettings = new LlamaRemoteSettings();
  protected LlamaRequestSettings requestSettings = new LlamaRequestSettings();

  public LlamaSettingsState() {
  }

  public static LlamaSettingsState getInstance() {
    LlamaSettingsState service = ApplicationManager.getApplication()
        .getService(LlamaSettingsState.class);
    service.localSettings.setCredentialsManager(new LlamaCredentialsManager("LOCAL"));
    service.remoteSettings.setCredentialsManager(new LlamaCredentialsManager("REMOTE"));
    return service;
  }

  @Override
  public LlamaSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
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
  public boolean isModified(LlamaServiceForm llamaServiceForm) {
    var llamaServerForm = llamaServiceForm.getServerPreferencesForm();
    var llamaRequestForm = llamaServiceForm.getRequestPreferencesForm();
    return localSettings.isModified(llamaServerForm.getLocalSettings())
        || localSettings.getCredentialsManager().isModified(llamaServerForm.getLocalApiKey())
        || remoteSettings.isModified(llamaServerForm.getRemoteSettings())
        || remoteSettings.getCredentialsManager()
        .isModified(llamaServerForm.getRemoteApikey())
        || runLocalServer != llamaServerForm.isRunLocalServer()
        || requestSettings.isModified(llamaRequestForm.getRequestSettings());
  }

  public void apply(LlamaServiceForm llamaServiceForm) {
    var llamaServerForm = llamaServiceForm.getServerPreferencesForm();
    runLocalServer = llamaServerForm.isRunLocalServer();
    localSettings = llamaServerForm.getLocalSettings();
    localSettings.getCredentialsManager().apply(llamaServerForm.getLocalApiKey());
    remoteSettings = llamaServerForm.getRemoteSettings();
    remoteSettings.getCredentialsManager().apply(llamaServerForm.getRemoteApikey());
    var llamaRequestsForm = llamaServiceForm.getRequestPreferencesForm();
    requestSettings = llamaRequestsForm.getRequestSettings();
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
