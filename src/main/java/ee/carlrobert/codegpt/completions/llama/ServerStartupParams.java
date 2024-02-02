package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import java.io.File;
import java.util.List;

public class ServerStartupParams {

  private final String serverPath;
  private final LlamaCompletionModel selectedModel;
  private final int contextLength;
  private final int threads;
  private final int port;
  private final List<String> additionalParameters;

  public ServerStartupParams(String serverPath, LlamaCompletionModel selectedModel,
      LlamaLocalSettings localSettings) {
    this.serverPath = serverPath;
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


  public String getServerPath() {
    return serverPath;
  }

  public boolean isUseCustomServer() {
    return !serverPath.equals(LlamaLocalSettings.BUNDLED_SERVER);
  }

  public String getServerFileName() {
    return serverPath.substring(serverPath.lastIndexOf(File.separator) + 1);
  }

  public String getServerDirectory() {
    return serverPath.substring(0, serverPath.lastIndexOf(File.separator) + 1);
  }
}
