package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.Nullable;

@Service
public final class YouCredentialsManager {

  private static final CredentialAttributes accountPasswordCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("ACCOUNT_PASSWORD");

  private String accountPassword;

  private YouCredentialsManager() {
    accountPassword = CredentialsUtil.getPassword(accountPasswordCredentialAttributes);
  }

  public static YouCredentialsManager getInstance() {
    return ApplicationManager.getApplication().getService(YouCredentialsManager.class);
  }

  public @Nullable String getAccountPassword() {
    return accountPassword;
  }

  public void setAccountPassword(String accountPassword) {
    this.accountPassword = accountPassword;
    CredentialsUtil.setPassword(accountPasswordCredentialAttributes, accountPassword);
  }
}
