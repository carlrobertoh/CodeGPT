package ee.carlrobert.codegpt.completions.llama;

import java.util.List;

public class LlamaServerStartupParams {

  private final String modelPath;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public LlamaServerStartupParams(
      String modelPath,
      int contextLength,
      int threads,
      int port,
      List<String> additionalParameters) {
    this.modelPath = modelPath;
    this.contextLength = contextLength;
    this.threads = threads;
    this.port = port;
    this.additionalParameters = additionalParameters;
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
