package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.settings.state.LocalSettings;
import java.util.List;

public class LlamaServerStartupParams {

  private final String modelPath;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public LlamaServerStartupParams(
      String modelPath,
      LlamaLocalSettings localSettings) {
    this.modelPath = modelPath;
    this.contextLength = localSettings.getContextSize();
    this.threads = localSettings.getThreads();
    this.port = localSettings.getServerPort();
    this.additionalParameters = localSettings.getListOfAdditionalParameters();
  }

  public String getModelPath() {
    return modelPath;
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
