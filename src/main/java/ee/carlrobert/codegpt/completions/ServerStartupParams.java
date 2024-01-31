package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.state.llama.LocalSettings;
import java.util.List;

public class ServerStartupParams {

  private final boolean useCustomModel;
  private final String customModelId;
  private final HuggingFaceModel selectedModel;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public ServerStartupParams(
      boolean useCustomModel, String customModelId,
      HuggingFaceModel selectedModel, LocalSettings localSettings) {
    this.useCustomModel = useCustomModel;
    this.customModelId = customModelId;
    this.selectedModel = selectedModel;
    this.contextLength = localSettings.getContextSize();
    this.threads = localSettings.getThreads();
    this.port = localSettings.getServerPort();
    this.additionalParameters = localSettings.getListOfAdditionalParameters();
  }

  public boolean isUseCustomModel() {
    return useCustomModel;
  }

  public String getCustomModelId() {
    return customModelId;
  }

  public HuggingFaceModel getSelectedModel() {
    return selectedModel;
  }

  public int getContextLength() {
    return contextLength;
  }

  public int getThreads() {
    return threads;
  }

  public int getPort() {
    return port;
  }

  public List<String> getAdditionalParameters() {
    return additionalParameters;
  }
}
