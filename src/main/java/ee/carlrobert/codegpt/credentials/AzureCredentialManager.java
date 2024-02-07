package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.settings.state.AzureSettings;

@Service
public final class AzureCredentialManager extends AbstractCredentialManager {

  public static final String API_KEY = "AZURE_OPENAI_API_KEY";
  public static final String ACTIVE_DIRECTORY_TOKEN = "AZURE_ACTIVE_DIRECTORY_TOKEN";

  private AzureCredentialManager() {
    super(API_KEY, ACTIVE_DIRECTORY_TOKEN);
  }

  public static AzureCredentialManager getInstance() {
    return ApplicationManager.getApplication().getService(AzureCredentialManager.class);
  }

  @Override
  public boolean isCredentialSet() {
    if (AzureSettings.getInstance().getState().isUseAzureApiKeyAuthentication()) {
      return isCredentialSet(ACTIVE_DIRECTORY_TOKEN);
    }
    return isCredentialSet(API_KEY);
  }

  @Override
  public String getCredential() {
    if (AzureSettings.getInstance().getState().isUseAzureActiveDirectoryAuthentication()) {
      return getActiveDirectoryToken();
    }
    return getApiKey();
  }

  public void setApiKey(String apiKey) {
    setCredential(API_KEY, apiKey);
  }

  public String getApiKey() {
    return getCredential(API_KEY);
  }

  public void setActiveDirectoryToken(String activeDirectoryToken) {
    setCredential(ACTIVE_DIRECTORY_TOKEN, activeDirectoryToken);
  }

  public String getActiveDirectoryToken() {
    return getCredential(ACTIVE_DIRECTORY_TOKEN);
  }
}