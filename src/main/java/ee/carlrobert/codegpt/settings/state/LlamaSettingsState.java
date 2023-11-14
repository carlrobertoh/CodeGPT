package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
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

  private static Integer getRandomAvailablePortOrDefault() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      return 8080;
    }
  }
}
