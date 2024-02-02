package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import java.util.List;

public class ServerStartupParams {

  private final LlamaCompletionModel selectedModel;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public ServerStartupParams(LlamaCompletionModel selectedModel, LlamaLocalSettings localSettings) {
    this.selectedModel = selectedModel;
    this.contextLength = localSettings.getContextSize();
    this.threads = localSettings.getThreads();
    this.port = localSettings.getServerPort();
    this.additionalParameters = localSettings.getListOfAdditionalParameters();
  }

  public LlamaCompletionModel getSelectedModel() {
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
