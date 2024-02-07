package ee.carlrobert.codegpt.credentials.managers;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.CredentialsService;

@Service
public final class AzureCredentialsManager extends CredentialsManager<AzureCredentials> {

  private final CredentialAttributes apiKeyCredentialsAttribute =
      credentialsService.createCredentialAttributes("AZURE_OPENAI_API_KEY");
  private final CredentialAttributes activeDirectoryTokenCredentialsAttribute =
      credentialsService.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");

  public AzureCredentialsManager() {
    init();
  }

  public AzureCredentialsManager(CredentialsService credentialsService) {
    super(credentialsService);
    init();
  }

  private void init() {
    credentials = new AzureCredentials(credentialsService.getPassword((apiKeyCredentialsAttribute)),
        credentialsService.getPassword((activeDirectoryTokenCredentialsAttribute)));
  }

  @Override
  public void apply(AzureCredentials credentials) {
    this.credentials = credentials;
    credentialsService.setPassword(apiKeyCredentialsAttribute, credentials.getApiKey());
    credentialsService.setPassword(activeDirectoryTokenCredentialsAttribute,
        credentials.getActiveDirectoryToken());
  }

  public static AzureCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(AzureCredentialsManager.class);
  }
}
