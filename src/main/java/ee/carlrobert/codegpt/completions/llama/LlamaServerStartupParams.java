package ee.carlrobert.codegpt.completions.llama;

import java.io.File;
import java.util.List;

public class LlamaServerStartupParams {

  private final String serverPath;
  private final boolean useCustomServer;
  private final String modelPath;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public LlamaServerStartupParams(
      String serverPath,
      boolean useCustomServer, String modelPath,
      int contextLength,
      int threads,
      int port,
      List<String> additionalParameters) {
    this.serverPath = serverPath;
    this.useCustomServer = useCustomServer;
    this.modelPath = modelPath;
    this.contextLength = contextLength;
    this.threads = threads;
    this.port = port;
    this.additionalParameters = additionalParameters;
  }

  public String getServerPath() {
    return serverPath;
  }

  public String getServerFileName() {
    return serverPath.substring(serverPath.lastIndexOf(File.separator) + 1);
  }

  public String getServerDirectory() {
    return serverPath.substring(0, serverPath.lastIndexOf(File.separator) + 1);
  }

  public boolean isUseCustomServer() {
    return useCustomServer;
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
