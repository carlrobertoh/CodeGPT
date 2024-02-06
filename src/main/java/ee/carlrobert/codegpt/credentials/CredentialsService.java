package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.Nullable;

@Service
public final class CredentialsService {

  private final PasswordSafe passwordSafe;

  public CredentialsService() {
    this.passwordSafe = PasswordSafe.getInstance();
  }

  public CredentialsService(PasswordSafe passwordSafe) {
    this.passwordSafe = passwordSafe;
  }

  public static CredentialsService getInstance() {
    return ApplicationManager.getApplication().getService(CredentialsService.class);
  }

  public CredentialAttributes createCredentialAttributes(String key) {
    return new CredentialAttributes(CredentialAttributesKt.generateServiceName("CodeGPT", key));
  }

  public void setPassword(CredentialAttributes credentialAttributes, String password) {
    passwordSafe.setPassword(credentialAttributes, password);
  }

  public @Nullable String getPassword(CredentialAttributes credentialAttributes) {
    return passwordSafe.getPassword(credentialAttributes);
  }
}
