package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class AzureCredentialsManager extends ApiKeyCredentialsManager {

  private static final CredentialAttributes azureOpenAIApiKeyCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("AZURE_OPENAI_API_KEY");
  private static final CredentialAttributes azureActiveDirectoryTokenCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");

  public AzureCredentialsManager() {
    super(azureOpenAIApiKeyCredentialAttributes,
        Set.of(azureActiveDirectoryTokenCredentialAttributes));
  }

  public @Nullable String getActiveDirectoryToken() {
    return getCredential(azureActiveDirectoryTokenCredentialAttributes);
  }

  public void apply(String apiKey, String activeDirectoryToken) {
    super.apply(apiKey);
    setCredential(azureActiveDirectoryTokenCredentialAttributes, activeDirectoryToken);
  }

  public String getSecret() {
    return AzureSettings.getInstance().isUseAzureActiveDirectoryAuthentication()
        ? getActiveDirectoryToken()
        : getApiKey();
  }

  @Override
  public boolean isCredentialSet() {
    if (AzureSettings.getInstance().isUseAzureApiKeyAuthentication()) {
      return super.isCredentialSet();
    }
    String token = getActiveDirectoryToken();
    return token != null && !token.isEmpty();
  }

  public boolean isModified(String apiKey, String activeDirectoryToken) {
    return super.isModified(Map.of(azureOpenAIApiKeyCredentialAttributes, apiKey,
        azureActiveDirectoryTokenCredentialAttributes, activeDirectoryToken));
  }

}
