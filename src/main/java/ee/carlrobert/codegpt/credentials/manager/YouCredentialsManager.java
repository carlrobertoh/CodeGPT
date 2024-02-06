package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.CredentialsService;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;

@Service
public final class YouCredentialsManager extends CredentialsManager<PasswordCredentials> {

  private final CredentialAttributes passwordCredentialAttributes =
      credentialsService.createCredentialAttributes("YOU_ACCOUNT_PASSWORD");

  public YouCredentialsManager() {
    init();
  }

  public YouCredentialsManager(CredentialsService credentialsService) {
    super(credentialsService);
    init();
  }

  private void init() {
    credentials =
        new PasswordCredentials(credentialsService.getPassword((passwordCredentialAttributes)));
  }

  @Override
  public void apply(PasswordCredentials credentials) {
    this.credentials = credentials;
    credentialsService.setPassword(passwordCredentialAttributes, credentials.getPassword());
  }

  public static YouCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(YouCredentialsManager.class);
  }

}
