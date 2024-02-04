package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.credentialStore.CredentialAttributes;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.CredentialsUtil;

public class ApiKeyCredentialsManager extends CredentialsManager<ApiKeyCredentials> {

  private final CredentialAttributes apiKeyCredentialsAttribute;

  public ApiKeyCredentialsManager(String prefix) {
    apiKeyCredentialsAttribute = CredentialsUtil.createCredentialAttributes(
        prefix + "_API_KEY");
    credentials = new ApiKeyCredentials(CredentialsUtil.getPassword((apiKeyCredentialsAttribute)));
  }

  @Override
  public void apply(ApiKeyCredentials credentials) {
    CredentialsUtil.setPassword(apiKeyCredentialsAttribute, credentials.getApiKey());
  }
}
