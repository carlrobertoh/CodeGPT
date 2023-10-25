package ee.carlrobert.codegpt.settings.state;

import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  private String llamaModelPath = "";
  private HuggingFaceModel huggingFaceModel = HuggingFaceModel.CODE_LLAMA_7B_Q4_K_M;
  private int serverPort = 8080;
  private String host = format("http://localhost:%d/completion", serverPort);
  private boolean overrideHost;

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

  public String getLlamaModelPath() {
    return llamaModelPath;
  }

  public void setLlamaModelPath(String llamaModelPath) {
    this.llamaModelPath = llamaModelPath;
  }

  public HuggingFaceModel getHuggingFaceModel() {
    return huggingFaceModel;
  }

  public void setHuggingFaceModel(HuggingFaceModel huggingFaceModel) {
    this.huggingFaceModel = huggingFaceModel;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public void setOverrideHost(boolean overrideHost) {
    this.overrideHost = overrideHost;
  }

  public boolean isOverrideHost() {
    return overrideHost;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }
}
