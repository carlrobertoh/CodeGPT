package ee.carlrobert.codegpt.settings.state.llama;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;
import static java.util.stream.Collectors.toList;

import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * All settings necessary for running a Llama server locally
 */
public class LlamaLocalSettings extends CommonSettings<LlamaCredentialsManager> {

  private Integer serverPort = getRandomAvailablePortOrDefault();
  private int contextSize = 2048;
  private int threads = 8;
  private String additionalCompileParameters = "";

  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;

  protected LlamaCompletionModel model = HuggingFaceModel.CODE_LLAMA_7B_Q4;

  public LlamaLocalSettings() {
  }

  public LlamaLocalSettings(LlamaCompletionModel model, PromptTemplate promptTemplate,
      Integer serverPort, int contextSize,
      int threads,
      String additionalCompileParameters) {
    this.model = model;
    this.serverPort = serverPort;
    this.contextSize = contextSize;
    this.threads = threads;
    this.additionalCompileParameters = additionalCompileParameters;
    this.promptTemplate = promptTemplate;
  }

  public boolean isModified(LlamaLocalSettings localSettings) {
    return super.isModified(localSettings)
        || !serverPort.equals(localSettings.getServerPort())
        || contextSize != localSettings.getContextSize()
        || threads != localSettings.getThreads()
        || !additionalCompileParameters.equals(localSettings.getAdditionalParameters())
        || !promptTemplate.equals(localSettings.getPromptTemplate())
        || areValuesDifferent(localSettings.getModel(), this.getModel());
  }

  private static Integer getRandomAvailablePortOrDefault() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      return 8080;
    }
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
    return additionalCompileParameters;
  }

  public void setAdditionalParameters(String additionalParameters) {
    this.additionalCompileParameters = additionalParameters;
  }

  public List<String> getListOfAdditionalParameters() {
    if (additionalCompileParameters.trim().isEmpty()) {
      return Collections.emptyList();
    }
    var parameters = additionalCompileParameters.split(",");
    return Arrays.stream(parameters)
        .map(String::trim)
        .collect(toList());
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public LlamaCompletionModel getModel() {
    return model;
  }

  public void setModel(LlamaCompletionModel model) {
    this.model = model;
  }


}
