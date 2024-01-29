package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.LlmModel;
import ee.carlrobert.codegpt.completions.llama.LlamaHuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  private boolean runLocalServer = true;
  private LlamaLocalSettings localSettings = new LlamaLocalSettings();
  private RemoteSettings remoteSettings = new RemoteSettings();

  // RequestSettings
  private int topK = 40;
  private double topP = 0.9;
  private double minP = 0.05;
  private double repeatPenalty = 1.1;

  public LlamaSettingsState() {
  }

  public static LlamaSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettingsState.class);
  }

  @Override
  public LlamaSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();
    var llamaServerPreferencesForm = serviceSelectionForm.getLlamaServerPreferencesForm();

    return localSettings.isModified(llamaServerPreferencesForm.getLocalSettings())
        || remoteSettings.isModified(llamaServerPreferencesForm.getRemoteSettings())
        || runLocalServer != llamaServerPreferencesForm.isRunLocalServer()
        || topK != requestPreferencesForm.getTopK()
        || topP != requestPreferencesForm.getTopP()
        || minP != requestPreferencesForm.getMinP()
        || repeatPenalty != requestPreferencesForm.getRepeatPenalty();
  }

  public void apply(ServiceSelectionForm serviceSelectionForm) {
    var llamaServerPreferencesForm = serviceSelectionForm.getLlamaServerPreferencesForm();
    localSettings = llamaServerPreferencesForm.getLocalSettings();
    remoteSettings = llamaServerPreferencesForm.getRemoteSettings();
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();
    topK = requestPreferencesForm.getTopK();
    topP = requestPreferencesForm.getTopP();
    minP = requestPreferencesForm.getMinP();
    repeatPenalty = requestPreferencesForm.getRepeatPenalty();
    runLocalServer = llamaServerPreferencesForm.isRunLocalServer();
  }

  public void reset(ServiceSelectionForm serviceSelectionForm) {
    var llamaServerPreferencesForm = serviceSelectionForm.getLlamaServerPreferencesForm();
    llamaServerPreferencesForm.setRemoteSettings(remoteSettings);
    llamaServerPreferencesForm.setLocalSettings(localSettings);
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();
    requestPreferencesForm.setTopK(topK);
    requestPreferencesForm.setTopP(topP);
    requestPreferencesForm.setMinP(minP);
    requestPreferencesForm.setRepeatPenalty(repeatPenalty);
    llamaServerPreferencesForm.setRunLocalServer(runLocalServer);
  }

  public boolean isRunLocalServer() {
    return runLocalServer;
  }

  public void setRunLocalServer(boolean runLocalServer) {
    this.runLocalServer = runLocalServer;
  }

  public int getTopK() {
    return topK;
  }

  public void setTopK(int topK) {
    this.topK = topK;
  }

  public double getTopP() {
    return topP;
  }

  public void setTopP(double topP) {
    this.topP = topP;
  }

  public double getMinP() {
    return minP;
  }

  public void setMinP(double minP) {
    this.minP = minP;
  }

  public double getRepeatPenalty() {
    return repeatPenalty;
  }

  public void setRepeatPenalty(double repeatPenalty) {
    this.repeatPenalty = repeatPenalty;
  }

  public LlamaLocalSettings getLocalSettings() {
    return localSettings;
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    this.localSettings = localSettings;
  }

  public RemoteSettings getRemoteSettings() {
    return remoteSettings;
  }

  public void setRemoteSettings(RemoteSettings remoteSettings) {
    this.remoteSettings = remoteSettings;
  }

  @Transient
  public String getLocalModelPath() {
    return localSettings.isUseCustomModel()
        ? localSettings.getCustomModel()
        : localSettings.getLlModel().getCode();
  }

  @Transient
  public LlamaHuggingFaceModel getLocalModel() {
    return localSettings.getLlModel();
  }

  public void setLocalModel(LlamaHuggingFaceModel llamaHuggingFaceModel) {
    localSettings.setLlmModel(llamaHuggingFaceModel);
  }

  public void setLocalModel(String modelPath) {
      localSettings.setCustomModel(modelPath);
  }

  public void setLocalUseCustomModel(boolean useCustomModel) {
      localSettings.setUseCustomModel(useCustomModel);
  }

  public boolean isLocalUseCustomModel() {
    return localSettings.isUseCustomModel();
  }
}
