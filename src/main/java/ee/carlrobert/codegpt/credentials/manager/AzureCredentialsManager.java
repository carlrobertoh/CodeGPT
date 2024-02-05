package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.CredentialsUtil;

@Service
public final class AzureCredentialsManager extends CredentialsManager<AzureCredentials> {

  private final CredentialAttributes apiKeyCredentialsAttribute =
      CredentialsUtil.createCredentialAttributes("AZURE_OPENAI_API_KEY");
  private final CredentialAttributes activeDirectoryTokenCredentialsAttribute =
      CredentialsUtil.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");

  public AzureCredentialsManager() {
    credentials = new AzureCredentials(CredentialsUtil.getPassword((apiKeyCredentialsAttribute)),
        CredentialsUtil.getPassword((activeDirectoryTokenCredentialsAttribute)));
  }

  @Override
  public void apply(AzureCredentials credentials) {
    this.credentials = credentials;
    CredentialsUtil.setPassword(apiKeyCredentialsAttribute, credentials.getApiKey());
    CredentialsUtil.setPassword(activeDirectoryTokenCredentialsAttribute,
        credentials.getActiveDirectoryToken());
  }

  public static AzureCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(AzureCredentialsManager.class);
  }
}
