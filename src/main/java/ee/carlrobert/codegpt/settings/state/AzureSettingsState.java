package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServicesSelectionForm;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettingsState implements PersistentStateComponent<AzureSettingsState> {

  private static final String BASE_PATH = "/openai/deployments/%s/chat/completions?api-version=%s";

  private String resourceName = "";
  private String deploymentId = "";
  private String apiVersion = "";
  private String baseHost = "https://%s.openai.azure.com";
  private String path = BASE_PATH;
  private boolean useAzureApiKeyAuthentication = true;
  private boolean useAzureActiveDirectoryAuthentication;

  public static AzureSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(AzureSettingsState.class);
  }

  @Override
  public AzureSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(ServicesSelectionForm servicesSelectionForm) {
    return servicesSelectionForm.isAzureActiveDirectoryAuthenticationSelected()
        != isUseAzureActiveDirectoryAuthentication()
        || servicesSelectionForm.isAzureApiKeyAuthenticationSelected()
        != isUseAzureApiKeyAuthentication()
        || AzureCredentialsManager.getInstance()
        .isModified(servicesSelectionForm.getAzureOpenAIApiKey(),
            servicesSelectionForm.getAzureActiveDirectoryToken())
        || !servicesSelectionForm.getAzureResourceName().equals(resourceName)
        || !servicesSelectionForm.getAzureDeploymentId().equals(deploymentId)
        || !servicesSelectionForm.getAzureApiVersion().equals(apiVersion)
        || !servicesSelectionForm.getAzureBaseHost().equals(baseHost)
        || !servicesSelectionForm.getAzurePath().equals(path);
  }

  public void apply(ServicesSelectionForm servicesSelectionForm) {
    useAzureActiveDirectoryAuthentication =
        servicesSelectionForm.isAzureActiveDirectoryAuthenticationSelected();
    useAzureApiKeyAuthentication = servicesSelectionForm.isAzureApiKeyAuthenticationSelected();

    resourceName = servicesSelectionForm.getAzureResourceName();
    deploymentId = servicesSelectionForm.getAzureDeploymentId();
    apiVersion = servicesSelectionForm.getAzureApiVersion();
    baseHost = servicesSelectionForm.getAzureBaseHost();
    path = servicesSelectionForm.getAzurePath();
  }

  public void reset(ServicesSelectionForm servicesSelectionForm) {
    servicesSelectionForm.setAzureApiKey(
        AzureCredentialsManager.getInstance().getAzureOpenAIApiKey());
    servicesSelectionForm.setAzureActiveDirectoryToken(
        AzureCredentialsManager.getInstance().getAzureActiveDirectoryToken());
    servicesSelectionForm.setAzureApiKeyAuthenticationSelected(useAzureApiKeyAuthentication);
    servicesSelectionForm.setAzureActiveDirectoryAuthenticationSelected(
        useAzureActiveDirectoryAuthentication);
    servicesSelectionForm.setAzureResourceName(resourceName);
    servicesSelectionForm.setAzureDeploymentId(deploymentId);
    servicesSelectionForm.setAzureApiVersion(apiVersion);
    servicesSelectionForm.setAzureBaseHost(baseHost);
    servicesSelectionForm.setAzurePath(path);
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
}
