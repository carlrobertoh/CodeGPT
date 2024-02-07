package ee.carlrobert.codegpt.settings.state;

import com.intellij.util.xmlb.annotations.Transient;
import java.util.Objects;

public class AzureSettingsState {

  private static final String BASE_PATH = "/openai/deployments/%s/chat/completions?api-version=%s";

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private String baseHost = "https://%s.openai.azure.com";
  private String path = BASE_PATH;
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

  @Transient
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

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AzureSettingsState that = (AzureSettingsState) o;
    return useAzureApiKeyAuthentication == that.useAzureApiKeyAuthentication
        && useAzureActiveDirectoryAuthentication == that.useAzureActiveDirectoryAuthentication
        && Objects.equals(resourceName, that.resourceName)
        && Objects.equals(deploymentId, that.deploymentId)
        && Objects.equals(apiVersion, that.apiVersion)
        && Objects.equals(baseHost, that.baseHost)
        && Objects.equals(path, that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceName, deploymentId, apiVersion, baseHost, path,
        useAzureApiKeyAuthentication, useAzureActiveDirectoryAuthentication);
  }
}
