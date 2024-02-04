package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.AzureServiceForm;
import ee.carlrobert.codegpt.settings.state.util.OpenAiRemoteSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettingsState extends
    OpenAiRemoteSettings<AzureCredentialsManager> implements
    PersistentStateComponent<AzureSettingsState> {

  private static final String BASE_PATH = "/openai/deployments/%s/chat/completions?api-version=%s";

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

  public AzureSettingsState() {
    super("https://%s.openai.azure.com", BASE_PATH, OpenAIChatCompletionModel.GPT_3_5,
        new AzureCredentialsManager());
  }

  public AzureSettingsState(String baseHost, String path, OpenAIChatCompletionModel model,
      AzureCredentialsManager credentialsManager, String resourceName, String deploymentId,
      String apiVersion, boolean useAzureApiKeyAuthentication,
      boolean useAzureActiveDirectoryAuthentication) {
    super(baseHost, path, model, credentialsManager);
    this.resourceName = resourceName;
    this.deploymentId = deploymentId;
    this.apiVersion = apiVersion;
    this.useAzureApiKeyAuthentication = useAzureApiKeyAuthentication;
    this.useAzureActiveDirectoryAuthentication = useAzureActiveDirectoryAuthentication;
  }

  public static AzureSettingsState getInstance() {
    return ApplicationManager.getApplication()
        .getService(AzureSettingsState.class);
  }

  @Override
  public AzureSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Transient
  public boolean isModified(AzureSettingsState settingsState) {
    return super.isModified(settingsState)
        || settingsState.isUseAzureActiveDirectoryAuthentication()
        != isUseAzureActiveDirectoryAuthentication()
        || settingsState.isUseAzureApiKeyAuthentication()
        != isUseAzureApiKeyAuthentication()
        || credentialsManager.isModified(settingsState.getCredentialsManager().getApiKey(),
        settingsState.getCredentialsManager().getActiveDirectoryToken())
        || !settingsState.getResourceName().equals(resourceName)
        || !settingsState.getDeploymentId().equals(deploymentId)
        || !settingsState.getApiVersion().equals(apiVersion);
  }

  public void apply(AzureSettingsState settingsState) {
    useAzureActiveDirectoryAuthentication =
        settingsState.isUseAzureActiveDirectoryAuthentication();
    useAzureApiKeyAuthentication = settingsState.isUseAzureApiKeyAuthentication();

    resourceName = settingsState.getResourceName();
    deploymentId = settingsState.getDeploymentId();
    apiVersion = settingsState.getApiVersion();
    baseHost = settingsState.getBaseHost();
    path = settingsState.getPath();
    model = settingsState.getModel();
    AzureCredentialsManager otherCredentials = settingsState.getCredentialsManager();
    credentialsManager.apply(otherCredentials.getApiKey(),
        otherCredentials.getActiveDirectoryToken());
  }

  public void reset(AzureServiceForm serviceSelectionForm) {
    serviceSelectionForm.setApiKey(credentialsManager.getApiKey());
    serviceSelectionForm.setAzureActiveDirectoryToken(credentialsManager.getActiveDirectoryToken());
    serviceSelectionForm.setAzureApiKeyAuthenticationSelected(useAzureApiKeyAuthentication);
    serviceSelectionForm.setAzureActiveDirectoryAuthenticationSelected(
        useAzureActiveDirectoryAuthentication);
    serviceSelectionForm.setAzureResourceName(resourceName);
    serviceSelectionForm.setAzureDeploymentId(deploymentId);
    serviceSelectionForm.setAzureApiVersion(apiVersion);
    serviceSelectionForm.setRemoteWithModelSettings(
        new OpenAiRemoteSettings<>(baseHost, path, model, credentialsManager));
  }

  public boolean isUsingCustomPath() {
    return !BASE_PATH.equals(path);
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public boolean isUseAzureApiKeyAuthentication() {
    return useAzureApiKeyAuthentication;
  }

  public void setUseAzureApiKeyAuthentication(boolean useAzureApiKeyAuthentication) {
    this.useAzureApiKeyAuthentication = useAzureApiKeyAuthentication;
  }

  public boolean isUseAzureActiveDirectoryAuthentication() {
    return useAzureActiveDirectoryAuthentication;
  }

  public void setUseAzureActiveDirectoryAuthentication(
      boolean useAzureActiveDirectoryAuthentication) {
    this.useAzureActiveDirectoryAuthentication = useAzureActiveDirectoryAuthentication;
  }
}
