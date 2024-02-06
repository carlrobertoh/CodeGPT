package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.credentialStore.CredentialAttributes;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.CredentialsService;

public class ApiKeyCredentialsManager extends CredentialsManager<ApiKeyCredentials> {

  private CredentialAttributes apiKeyCredentialsAttribute;

  public ApiKeyCredentialsManager(String prefix) {
    super();
    init(prefix);
  }

  public ApiKeyCredentialsManager(String prefix, CredentialsService credentialsService) {
    super(credentialsService);
    init(prefix);
  }

  private void init(String prefix) {
    apiKeyCredentialsAttribute = credentialsService.createCredentialAttributes(
        prefix + "_API_KEY");
    credentials = new ApiKeyCredentials(
        credentialsService.getPassword((apiKeyCredentialsAttribute)));
  }

  @Override
  public void apply(ApiKeyCredentials credentials) {
    this.credentials = credentials;
    credentialsService.setPassword(apiKeyCredentialsAttribute, credentials.getApiKey());
  }
}
