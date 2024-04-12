package ee.carlrobert.codegpt.settings.service.azure;

import ee.carlrobert.codegpt.credentials.CredentialsStore;
import java.util.Objects;

public class AzureSettingsState {

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

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

  public CredentialsStore.CredentialKey getCredentialKey() {
    return isUseAzureApiKeyAuthentication()
            ? CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY
            : CredentialsStore.CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN;
  }

  public boolean isCredentialSet() {
    return CredentialsStore.INSTANCE.isCredentialSet(getCredentialKey());
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
        && Objects.equals(apiVersion, that.apiVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceName, deploymentId, apiVersion, useAzureApiKeyAuthentication,
        useAzureActiveDirectoryAuthentication);
  }
}
