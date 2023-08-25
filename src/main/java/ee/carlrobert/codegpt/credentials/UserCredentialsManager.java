package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import org.jetbrains.annotations.Nullable;

public class UserCredentialsManager {

  private static final CredentialAttributes accountPasswordCredentialAttributes = CredentialsUtil.createCredentialAttributes("ACCOUNT_PASSWORD");

  private static UserCredentialsManager instance;

  private String accountPassword;

  private UserCredentialsManager() {
    accountPassword = CredentialsUtil.getPassword(accountPasswordCredentialAttributes);
  }

  public static UserCredentialsManager getInstance() {
    if (instance == null) {
      instance = new UserCredentialsManager();
    }
    return instance;
  }

  public @Nullable String getAccountPassword() {
    return accountPassword;
  }

  public void setAccountPassword(String accountPassword) {
    this.accountPassword = accountPassword;
    CredentialsUtil.setPassword(accountPasswordCredentialAttributes, accountPassword);
  }
}
