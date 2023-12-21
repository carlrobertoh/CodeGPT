package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import org.jetbrains.annotations.Nullable;

@Service
public final class AzureCredentialsManager {

  private static final CredentialAttributes azureOpenAIApiKeyCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("AZURE_OPENAI_API_KEY");
  private static final CredentialAttributes azureActiveDirectoryTokenCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");

  private String azureOpenAIApiKey;
  private String azureActiveDirectoryToken;

  private AzureCredentialsManager() {
    azureOpenAIApiKey = CredentialsUtil.getPassword(azureOpenAIApiKeyCredentialAttributes);
    azureActiveDirectoryToken =
        CredentialsUtil.getPassword(azureActiveDirectoryTokenCredentialAttributes);
  }

  public static AzureCredentialsManager getInstance() {
    return ApplicationManager.getApplication().getService(AzureCredentialsManager.class);
  }

  public String getSecret() {
    return AzureSettingsState.getInstance().isUseAzureActiveDirectoryAuthentication()
        ? azureActiveDirectoryToken
        : azureOpenAIApiKey;
  }

  public @Nullable String getAzureOpenAIApiKey() {
    return azureOpenAIApiKey;
  }

  public void setApiKey(String azureOpenAIApiKey) {
    this.azureOpenAIApiKey = azureOpenAIApiKey;
    CredentialsUtil.setPassword(azureOpenAIApiKeyCredentialAttributes, azureOpenAIApiKey);
  }

  public @Nullable String getAzureActiveDirectoryToken() {
    return azureActiveDirectoryToken;
  }

  public void setAzureActiveDirectoryToken(String azureActiveDirectoryToken) {
    this.azureActiveDirectoryToken = azureActiveDirectoryToken;
    CredentialsUtil.setPassword(
        azureActiveDirectoryTokenCredentialAttributes,
        azureActiveDirectoryToken);
  }

  public boolean isCredentialSet() {
    if (AzureSettingsState.getInstance().isUseAzureApiKeyAuthentication()) {
      return isKeySet();
    }
    return isTokenSet();
  }

  private boolean isTokenSet() {
    return azureActiveDirectoryToken != null && !azureActiveDirectoryToken.isEmpty();
  }

  private boolean isKeySet() {
    return azureOpenAIApiKey != null && !azureOpenAIApiKey.isEmpty();
  }
}