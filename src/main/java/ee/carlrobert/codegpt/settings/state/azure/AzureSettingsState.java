package ee.carlrobert.codegpt.settings.state.azure;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;

public class AzureSettingsState extends RemoteSettings<AzureCredentials> {

  private static final String BASE_PATH = "/openai/deployments/%s/chat/completions?api-version=%s";

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

  public AzureSettingsState() {
    super("https://%s.openai.azure.com", BASE_PATH, new AzureCredentials());
  }

  public AzureSettingsState(String baseHost, String path,
      AzureCredentials credentials, String resourceName, String deploymentId,
      String apiVersion, boolean useAzureApiKeyAuthentication,
      boolean useAzureActiveDirectoryAuthentication) {
    super(baseHost, path, credentials);
    this.resourceName = resourceName;
    this.deploymentId = deploymentId;
    this.apiVersion = apiVersion;
    this.useAzureApiKeyAuthentication = useAzureApiKeyAuthentication;
    this.useAzureActiveDirectoryAuthentication = useAzureActiveDirectoryAuthentication;
  }

  @Transient
  public boolean isModified(AzureSettingsState settingsState) {
    return super.isModified(settingsState)
        || settingsState.isUseAzureActiveDirectoryAuthentication()
        != isUseAzureActiveDirectoryAuthentication()
        || settingsState.isUseAzureApiKeyAuthentication()
        != isUseAzureApiKeyAuthentication()
        || credentials.isModified(settingsState.getCredentials())
        || !StringUtil.equals(resourceName, settingsState.getResourceName())
        || !StringUtil.equals(deploymentId, settingsState.getDeploymentId())
        || !StringUtil.equals(apiVersion, settingsState.getApiVersion());
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
