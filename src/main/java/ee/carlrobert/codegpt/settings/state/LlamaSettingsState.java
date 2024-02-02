package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.llama.LlamaRequestsForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaLocalOrRemoteServiceForm;
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

  public boolean isModified(LlamaLocalOrRemoteServiceForm llamaLocalOrRemoteServiceForm,
      LlamaRequestsForm llamaRequestsForm) {
    return localSettings.isModified(llamaLocalOrRemoteServiceForm.getLocalSettings())
        || localSettings.getCredentialsManager().isModified(llamaLocalOrRemoteServiceForm.getLocalApiKey())
        || remoteSettings.isModified(llamaLocalOrRemoteServiceForm.getRemoteSettings())
        || remoteSettings.getCredentialsManager()
        .isModified(llamaLocalOrRemoteServiceForm.getRemoteApikey())
        || runLocalServer != llamaLocalOrRemoteServiceForm.isRunLocalServer()
        || requestSettings.isModified(llamaRequestsForm.getRequestSettings());
  }

  public void apply(LlamaLocalOrRemoteServiceForm llamaLocalOrRemoteServiceForm,
      LlamaRequestsForm llamaRequestsForm) {
    runLocalServer = llamaLocalOrRemoteServiceForm.isRunLocalServer();
    localSettings = llamaLocalOrRemoteServiceForm.getLocalSettings();
    localSettings.getCredentialsManager().apply(llamaLocalOrRemoteServiceForm.getLocalApiKey());
    remoteSettings = llamaLocalOrRemoteServiceForm.getRemoteSettings();
    remoteSettings.getCredentialsManager().apply(llamaLocalOrRemoteServiceForm.getRemoteApikey());
    requestSettings = llamaRequestsForm.getRequestSettings();
  }

  public void reset(LlamaLocalOrRemoteServiceForm llamaLocalOrRemoteServiceForm,
      LlamaRequestsForm llamaRequestsForm) {
    llamaLocalOrRemoteServiceForm.setRemoteSettings(remoteSettings);
    llamaLocalOrRemoteServiceForm.setLocalSettings(localSettings);
    llamaLocalOrRemoteServiceForm.setRunLocalServer(runLocalServer);
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
