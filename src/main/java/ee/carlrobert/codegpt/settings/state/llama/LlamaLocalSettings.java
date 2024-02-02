package ee.carlrobert.codegpt.settings.state.llama;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;
import static java.util.stream.Collectors.toList;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * All settings necessary for running a Llama server locally
 */
public class LlamaLocalSettings extends CommonSettings<LlamaCredentialsManager> {

  public static final String BUNDLED_SERVER =
      CodeGPTPlugin.getLlamaSourcePath() + File.separator + "server";

  private boolean serverRunning = false;
  private Integer serverPort = getRandomAvailablePortOrDefault();
  private int contextSize = 2048;
  private int threads = 8;
  private String additionalCompileParameters = "";

  private PromptTemplate chatPromptTemplate = PromptTemplate.LLAMA;
  private InfillPromptTemplate infillPromptTemplate = InfillPromptTemplate.LLAMA;

  protected LlamaCompletionModel model = HuggingFaceModel.CODE_LLAMA_7B_Q4;
  private String serverPath = BUNDLED_SERVER;


  public LlamaLocalSettings() {
  }

  public LlamaLocalSettings(
      String serverPath,
      LlamaCompletionModel model,
      PromptTemplate chatPromptTemplate,
      InfillPromptTemplate infillPromptTemplate,
      Integer serverPort, int contextSize,
      int threads,
      String additionalCompileParameters) {
    this.serverPath = serverPath;
    this.model = model;
    this.serverPort = serverPort;
    this.contextSize = contextSize;
    this.threads = threads;
    this.additionalCompileParameters = additionalCompileParameters;
    this.chatPromptTemplate = chatPromptTemplate;
    this.infillPromptTemplate = infillPromptTemplate;
  }

  public boolean isModified(LlamaLocalSettings localSettings) {
    return super.isModified(localSettings)
        || !serverPath.equals(localSettings.getServerPath())
        || !serverPort.equals(localSettings.getServerPort())
        || contextSize != localSettings.getContextSize()
        || threads != localSettings.getThreads()
        || !additionalCompileParameters.equals(localSettings.getAdditionalCompileParameters())
        || !chatPromptTemplate.equals(localSettings.getChatPromptTemplate())
        || areValuesDifferent(localSettings.getModel(), this.getModel())
        || !infillPromptTemplate.equals(localSettings.getInfillPromptTemplate());
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

  public String getAdditionalCompileParameters() {
    return additionalCompileParameters;
  }

  public void setAdditionalCompileParameters(String additionalCompileParameters) {
    this.additionalCompileParameters = additionalCompileParameters;
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

  public PromptTemplate getChatPromptTemplate() {
    return chatPromptTemplate;
  }

  public void setChatPromptTemplate(PromptTemplate promptTemplate) {
    this.chatPromptTemplate = promptTemplate;
  }

  public LlamaCompletionModel getModel() {
    return model;
  }

  public void setModel(LlamaCompletionModel model) {
    this.model = model;
  }

  public String getServerPath() {
    return serverPath;
  }

  public void setServerPath(String serverPath) {
    this.serverPath = serverPath;
  }

  @Transient
  public boolean isUseCustomServer() {
    return !getServerPath().equals(BUNDLED_SERVER);
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return infillPromptTemplate;
  }

  public void setInfillPromptTemplate(
      InfillPromptTemplate infillPromptTemplate) {
    this.infillPromptTemplate = infillPromptTemplate;
  }

  public boolean isServerRunning() {
    return serverRunning;
  }

  public void setServerRunning(boolean serverRunning) {
    this.serverRunning = serverRunning;
  }
}
