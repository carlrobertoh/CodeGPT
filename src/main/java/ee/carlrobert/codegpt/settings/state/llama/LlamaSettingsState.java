package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState extends
    LlamaServiceSettingsState implements PersistentStateComponent<LlamaSettingsState> {

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

  @Transient
  public String getLocalModelPath() {
    return localSettings.isUseCustomModel()
        ? localSettings.getCustomModel()
        : localSettings.getLlModel().getCode();
  }

  @Transient
  public HuggingFaceModel getLocalModel() {
    return localSettings.getLlModel();
  }

  public void setLocalModel(HuggingFaceModel huggingFaceModel) {
    localSettings.setLlmModel(huggingFaceModel);
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

  @Transient
  public PromptTemplate getActualPromptTemplate() {
    if (isRunLocalServer()) {
      return localSettings.isUseCustomModel()
          ? localSettings.getPromptTemplate()
          : LlamaModel.findByHuggingFaceModel(localSettings.getLlModel()).getPromptTemplate();
    }
    return remoteSettings.getPromptTemplate();
  }
}
