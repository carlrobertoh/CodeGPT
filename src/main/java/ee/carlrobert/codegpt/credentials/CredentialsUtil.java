package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import org.jetbrains.annotations.Nullable;

public class CredentialsUtil {

  private static final PasswordSafe passwordSafe = PasswordSafe.getInstance();

  public static CredentialAttributes createCredentialAttributes(String key) {
    return new CredentialAttributes(CredentialAttributesKt.generateServiceName("CodeGPT", key));
  }

  public static void setPassword(CredentialAttributes credentialAttributes, String password) {
    passwordSafe.setPassword(credentialAttributes, password);
  }

  public static @Nullable String getPassword(CredentialAttributes credentialAttributes) {
    return passwordSafe.getPassword(credentialAttributes);
  }
}
