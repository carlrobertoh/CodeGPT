package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import java.io.IOException;
import java.net.ServerSocket;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  private boolean useCustomModel;
  private String customLlamaModelPath = "";
  private HuggingFaceModel huggingFaceModel = HuggingFaceModel.CODE_LLAMA_7B_Q4;
  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;
  private Integer serverPort = getRandomAvailablePortOrDefault();
  private int contextSize = 2048;
  private int threads = 8;
  private String additionalParameters = "";

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
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    return serverPort != serviceSelectionForm.getLlamaServerPort()
        || contextSize != serviceSelectionForm.getContextSize()
        || threads != serviceSelectionForm.getThreads()
        || !additionalParameters.equals(serviceSelectionForm.getAdditionalParameters())
        || huggingFaceModel != modelPreferencesForm.getSelectedModel()
        || !promptTemplate.equals(modelPreferencesForm.getPromptTemplate())
        || useCustomModel != modelPreferencesForm.isUseCustomLlamaModel()
        || !customLlamaModelPath.equals(modelPreferencesForm.getCustomLlamaModelPath());
  }

  public void apply(ServiceSelectionForm serviceSelectionForm) {
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    customLlamaModelPath = modelPreferencesForm.getCustomLlamaModelPath();
    huggingFaceModel = modelPreferencesForm.getSelectedModel();
    useCustomModel = modelPreferencesForm.isUseCustomLlamaModel();
    promptTemplate = modelPreferencesForm.getPromptTemplate();
    serverPort = serviceSelectionForm.getLlamaServerPort();
    contextSize = serviceSelectionForm.getContextSize();
    threads = serviceSelectionForm.getThreads();
    additionalParameters = serviceSelectionForm.getAdditionalParameters();
  }

  public void reset(ServiceSelectionForm serviceSelectionForm) {
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    modelPreferencesForm.setSelectedModel(huggingFaceModel);
    modelPreferencesForm.setCustomLlamaModelPath(customLlamaModelPath);
    modelPreferencesForm.setUseCustomLlamaModel(useCustomModel);
    modelPreferencesForm.setPromptTemplate(promptTemplate);
    serviceSelectionForm.setLlamaServerPort(serverPort);
    serviceSelectionForm.setContextSize(contextSize);
    serviceSelectionForm.setThreads(threads);
    serviceSelectionForm.setAdditionalParameters(additionalParameters);
  }

  public boolean isUseCustomModel() {
    return useCustomModel;
  }

  public void setUseCustomModel(boolean useCustomModel) {
    this.useCustomModel = useCustomModel;
  }

  public String getCustomLlamaModelPath() {
    return customLlamaModelPath;
  }

  public void setCustomLlamaModelPath(String customLlamaModelPath) {
    this.customLlamaModelPath = customLlamaModelPath;
  }

  public HuggingFaceModel getHuggingFaceModel() {
    return huggingFaceModel;
  }

  public void setHuggingFaceModel(HuggingFaceModel huggingFaceModel) {
    this.huggingFaceModel = huggingFaceModel;
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }

  public int getContextSize() {
    return contextSize;
  }

  public void setContextSize(int contextSize) {
    this.contextSize = contextSize;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public String getAdditionalParameters() {
    return additionalParameters;
  }

  public void setAdditionalParameters(String additionalParameters) {
    this.additionalParameters = additionalParameters;
  }

  private static Integer getRandomAvailablePortOrDefault() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      return 8080;
    }
  }
}
