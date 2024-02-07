package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

abstract class AbstractCredentialManager {

  private static final PasswordSafe passwordSafe = PasswordSafe.getInstance();

  private final Map<String, CredentialAttributes> credentialMapping;

  protected AbstractCredentialManager(String... keys) {
    credentialMapping = Stream.of(keys).collect(Collectors.toMap(
        key -> key,
        key -> new CredentialAttributes(CredentialAttributesKt.generateServiceName("CodeGPT", key))
    ));
  }

  public abstract boolean isCredentialSet();

  protected boolean isCredentialSet(String key) {
    var credential = getCredential(key);
    return credential != null && !credential.isEmpty();
  }

  public abstract String getCredential();

  protected @Nullable String getCredential(String key) {
    return passwordSafe.getPassword(credentialMapping.get(key));
  }

  protected void setCredential(String key, String credential) {
    passwordSafe.setPassword(credentialMapping.get(key), credential);
  }
}
