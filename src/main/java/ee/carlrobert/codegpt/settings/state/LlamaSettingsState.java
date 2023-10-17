package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  private String llamaModelPath = "";
  private LlamaModel llamaModel = LlamaModel.CODE_LLAMA_7B;
  private int serverPort = 8080;

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

  public LlamaModel getLlamaModel() {
    return llamaModel;
  }

  public void setLlamaModel(LlamaModel llamaModel) {
    this.llamaModel = llamaModel;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }
}
