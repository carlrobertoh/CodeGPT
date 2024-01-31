package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import java.io.IOException;
import java.net.ServerSocket;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettingsState implements PersistentStateComponent<LlamaSettingsState> {

  private boolean runLocalServer = true;
  private boolean useCustomModel;
  private String customLlamaModelPath = "";
  private HuggingFaceModel huggingFaceModel = HuggingFaceModel.CODE_LLAMA_7B_Q4;
  private PromptTemplate localModelPromptTemplate = PromptTemplate.LLAMA;
  private PromptTemplate remoteModelPromptTemplate = PromptTemplate.LLAMA;
  private InfillPromptTemplate infillPromptTemplate = InfillPromptTemplate.LLAMA;
  private String baseHost = "http://localhost";
  private Integer serverPort = getRandomAvailablePortOrDefault();
  private int contextSize = 2048;
  private int threads = 8;
  private String additionalParameters = "";
  private int topK = 40;
  private double topP = 0.9;
  private double minP = 0.05;
  private double repeatPenalty = 1.1;

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

  public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();

    return !serviceSelectionForm.getLlamaServerPreferencesForm().getApiKey()
        .equals(LlamaCredentialsManager.getInstance().getApiKey())
        || runLocalServer != serviceSelectionForm.isLlamaRunLocalServer()
        || !remoteModelPromptTemplate.equals(serviceSelectionForm.getLlamaPromptTemplate())
        || !baseHost.equals(serviceSelectionForm.getLlamaBaseHost())
        || serverPort != serviceSelectionForm.getLlamaServerPort()
        || contextSize != serviceSelectionForm.getContextSize()
        || threads != serviceSelectionForm.getThreads()
        || !additionalParameters.equals(serviceSelectionForm.getAdditionalParameters())
        || huggingFaceModel != modelPreferencesForm.getSelectedModel()
        || topK != requestPreferencesForm.getTopK()
        || topP != requestPreferencesForm.getTopP()
        || minP != requestPreferencesForm.getMinP()
        || repeatPenalty != requestPreferencesForm.getRepeatPenalty()
        || !localModelPromptTemplate.equals(modelPreferencesForm.getPromptTemplate())
        || useCustomModel != modelPreferencesForm.isUseCustomLlamaModel()
        || !customLlamaModelPath.equals(modelPreferencesForm.getCustomLlamaModelPath());
  }

  public void apply(ServiceSelectionForm serviceSelectionForm) {
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    customLlamaModelPath = modelPreferencesForm.getCustomLlamaModelPath();
    customLlamaModelPath = modelPreferencesForm.getCustomLlamaModelPath();
    huggingFaceModel = modelPreferencesForm.getSelectedModel();
    useCustomModel = modelPreferencesForm.isUseCustomLlamaModel();
    localModelPromptTemplate = modelPreferencesForm.getPromptTemplate();
    remoteModelPromptTemplate = serviceSelectionForm.getLlamaPromptTemplate();
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();
    topK = requestPreferencesForm.getTopK();
    topP = requestPreferencesForm.getTopP();
    minP = requestPreferencesForm.getMinP();
    repeatPenalty = requestPreferencesForm.getRepeatPenalty();
    runLocalServer = serviceSelectionForm.isLlamaRunLocalServer();
    baseHost = serviceSelectionForm.getLlamaBaseHost();
    serverPort = serviceSelectionForm.getLlamaServerPort();
    contextSize = serviceSelectionForm.getContextSize();
    threads = serviceSelectionForm.getThreads();
    additionalParameters = serviceSelectionForm.getAdditionalParameters();
  }

  public void reset(ServiceSelectionForm serviceSelectionForm) {
    var modelPreferencesForm = serviceSelectionForm.getLlamaModelPreferencesForm();
    modelPreferencesForm.setSelectedModel(huggingFaceModel);
    modelPreferencesForm.setCustomLlamaModelPath(customLlamaModelPath);
    modelPreferencesForm.setUseCustomLlamaModel(useCustomModel);
    modelPreferencesForm.setPromptTemplate(localModelPromptTemplate);
    var requestPreferencesForm = serviceSelectionForm.getLlamaRequestPreferencesForm();
    requestPreferencesForm.setTopK(topK);
    requestPreferencesForm.setTopP(topP);
    requestPreferencesForm.setMinP(minP);
    requestPreferencesForm.setRepeatPenalty(repeatPenalty);
    serviceSelectionForm.setLlamaRunLocalServer(runLocalServer);
    serviceSelectionForm.setLlamaBaseHost(baseHost);
    serviceSelectionForm.setLlamaServerPort(serverPort);
    serviceSelectionForm.setLlamaPromptTemplate(remoteModelPromptTemplate);
    serviceSelectionForm.setContextSize(contextSize);
    serviceSelectionForm.setThreads(threads);
    serviceSelectionForm.setAdditionalParameters(additionalParameters);
    serviceSelectionForm.getLlamaServerPreferencesForm().setApiKey(
        LlamaCredentialsManager.getInstance().getApiKey());
  }

  public boolean isUseCustomModel() {
    return useCustomModel;
  }

  public void setUseCustomModel(boolean useCustomModel) {
    this.useCustomModel = useCustomModel;
  }

  public String getCustomLlamaModelPath() {
    return customLlamaModelPath;
  }

  public void setCustomLlamaModelPath(String customLlamaModelPath) {
    this.customLlamaModelPath = customLlamaModelPath;
  }

  public HuggingFaceModel getHuggingFaceModel() {
    return huggingFaceModel;
  }

  public void setHuggingFaceModel(HuggingFaceModel huggingFaceModel) {
    this.huggingFaceModel = huggingFaceModel;
  }

  public PromptTemplate getLocalModelPromptTemplate() {
    return localModelPromptTemplate;
  }

  public void setLocalModelPromptTemplate(
      PromptTemplate localModelPromptTemplate) {
    this.localModelPromptTemplate = localModelPromptTemplate;
  }

  public boolean isRunLocalServer() {
    return runLocalServer;
  }

  public void setRunLocalServer(boolean runLocalServer) {
    this.runLocalServer = runLocalServer;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public PromptTemplate getRemoteModelPromptTemplate() {
    return remoteModelPromptTemplate;
  }

  public void setRemoteModelPromptTemplate(
      PromptTemplate remoteModelPromptTemplate) {
    this.remoteModelPromptTemplate = remoteModelPromptTemplate;
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
    return additionalParameters;
  }

  public void setAdditionalParameters(String additionalParameters) {
    this.additionalParameters = additionalParameters;
  }

  public int getTopK() {
    return topK;
  }

  public void setTopK(int topK) {
    this.topK = topK;
  }

  public double getTopP() {
    return topP;
  }

  public void setTopP(double topP) {
    this.topP = topP;
  }

  public double getMinP() {
    return minP;
  }

  public void setMinP(double minP) {
    this.minP = minP;
  }

  public double getRepeatPenalty() {
    return repeatPenalty;
  }

  public void setRepeatPenalty(double repeatPenalty) {
    this.repeatPenalty = repeatPenalty;
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return infillPromptTemplate;
  }

  public void setInfillPromptTemplate(InfillPromptTemplate infillPromptTemplate) {
    this.infillPromptTemplate = infillPromptTemplate;
  }

  private static Integer getRandomAvailablePortOrDefault() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      return 8080;
    }
  }
}
