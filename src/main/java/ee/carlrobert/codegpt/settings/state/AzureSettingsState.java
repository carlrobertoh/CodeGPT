package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.AzureServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.ServicesSelectionForm;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettingsState extends RemoteSettings<AzureCredentialsManager> implements
    PersistentStateComponent<AzureSettingsState> {

  private static final String BASE_PATH = "/openai/deployments/%s/chat/completions?api-version=%s";

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

  public AzureSettingsState() {
    super("https://%s.openai.azure.com", BASE_PATH);
  }

  public static AzureSettingsState getInstance() {
    AzureSettingsState service = ApplicationManager.getApplication()
        .getService(AzureSettingsState.class);
    service.setCredentialsManager(new AzureCredentialsManager());
    return service;
  }

  @Override
  public AzureSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(AzureServiceSelectionForm serviceSelectionForm) {
    return serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected()
        != isUseAzureActiveDirectoryAuthentication()
        || serviceSelectionForm.isAzureApiKeyAuthenticationSelected()
        != isUseAzureApiKeyAuthentication()
        || credentialsManager.isModified(serviceSelectionForm.getAzureOpenAIApiKey(),
            serviceSelectionForm.getAzureActiveDirectoryToken())
        || !serviceSelectionForm.getAzureResourceName().equals(resourceName)
        || !serviceSelectionForm.getAzureDeploymentId().equals(deploymentId)
        || !serviceSelectionForm.getAzureApiVersion().equals(apiVersion)
        || !serviceSelectionForm.getAzureBaseHost().equals(baseHost)
        || !serviceSelectionForm.getAzurePath().equals(path);
  }

  public void apply(AzureServiceSelectionForm serviceSelectionForm) {
    useAzureActiveDirectoryAuthentication =
        serviceSelectionForm.isAzureActiveDirectoryAuthenticationSelected();
    useAzureApiKeyAuthentication = serviceSelectionForm.isAzureApiKeyAuthenticationSelected();

    resourceName = serviceSelectionForm.getAzureResourceName();
    deploymentId = serviceSelectionForm.getAzureDeploymentId();
    apiVersion = serviceSelectionForm.getAzureApiVersion();
    baseHost = serviceSelectionForm.getAzureBaseHost();
    path = serviceSelectionForm.getAzurePath();
  }

  public void reset(AzureServiceSelectionForm serviceSelectionForm) {
    serviceSelectionForm.setAzureApiKey(credentialsManager.getApiKey());
    serviceSelectionForm.setAzureActiveDirectoryToken(credentialsManager.getActiveDirectoryToken());
    serviceSelectionForm.setAzureApiKeyAuthenticationSelected(useAzureApiKeyAuthentication);
    serviceSelectionForm.setAzureActiveDirectoryAuthenticationSelected(
        useAzureActiveDirectoryAuthentication);
    serviceSelectionForm.setAzureResourceName(resourceName);
    serviceSelectionForm.setAzureDeploymentId(deploymentId);
    serviceSelectionForm.setAzureApiVersion(apiVersion);
    serviceSelectionForm.setAzureBaseHost(baseHost);
    serviceSelectionForm.setAzurePath(path);
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
