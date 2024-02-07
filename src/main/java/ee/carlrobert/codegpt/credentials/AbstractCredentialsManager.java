package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

abstract class AbstractCredentialsManager {

  private static final PasswordSafe passwordSafe = PasswordSafe.getInstance();

  private final Map<String, CredentialAttributes> credentialMapping;
  private final Map<String, String> credentialCache = new ConcurrentHashMap<>();

  protected AbstractCredentialsManager(String... keys) {
    credentialMapping = Stream.of(keys).collect(Collectors.toConcurrentMap(
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
    String cachedCredential = credentialCache.get(key);
    if (cachedCredential != null) {
      return cachedCredential;
    }

    String credential = passwordSafe.getPassword(credentialMapping.get(key));
    if (credential != null) {
      credentialCache.put(key, credential);
    }
    return credential;
  }

  protected void setCredential(String key, String credential) {
    passwordSafe.setPassword(credentialMapping.get(key), credential);
    credentialCache.put(key, credential);
  }
}
