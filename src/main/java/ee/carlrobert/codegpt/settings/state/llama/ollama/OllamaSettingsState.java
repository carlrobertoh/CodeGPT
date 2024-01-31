package ee.carlrobert.codegpt.settings.state.llama.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.credentials.OllamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.llama.ServiceSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OllamaSettings", storages = @Storage("CodeGPT_CodeGPT_OllamaSettings.xml"))
public class OllamaSettingsState extends ServiceSettingsState implements
    PersistentStateComponent<OllamaSettingsState> {

  public OllamaSettingsState() {
  }

  public static OllamaSettingsState getInstance() {
    OllamaSettingsState service = ApplicationManager.getApplication()
        .getService(OllamaSettingsState.class);
    service.localSettings.setCredentialsManager(new OllamaCredentialsManager());
    service.remoteSettings.setCredentialsManager(new OllamaCredentialsManager());
    return service;
  }

  @Override
  public OllamaSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OllamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Transient
  public String getLocalModelTag() {
    return localSettings.isUseCustomModel()
        ? localSettings.getCustomModel()
        : localSettings.getLlModel().getModelTag();
  }

  @Transient
  public HuggingFaceModel getLocalModel() {
    return localSettings.getLlModel();
  }

  @Transient
  public PromptTemplate getActualPromptTemplate() {
    if (isRunLocalServer()) {
      return localSettings.isUseCustomModel()
          ? localSettings.getPromptTemplate()
          : LlamaModel.findByHuggingFaceModel(localSettings.getLlModel())
              .getPromptTemplate();
    }
    return remoteSettings.getPromptTemplate();
  }

  @Transient
  public String getLocalActualModel() {
    return localSettings.isUseCustomModel() ? localSettings.getCustomModel()
        : localSettings.getLlModel().getModelTag();
  }
}
