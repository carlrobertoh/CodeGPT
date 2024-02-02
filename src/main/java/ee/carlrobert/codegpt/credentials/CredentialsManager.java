package ee.carlrobert.codegpt.credentials;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import com.intellij.credentialStore.CredentialAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CredentialsManager {

  protected Set<CredentialAttributes> credentialAttributes;
  protected Map<CredentialAttributes, String> credentials;

  public CredentialsManager(Set<CredentialAttributes> credentialAttributes) {
    this.credentialAttributes = credentialAttributes;
    this.credentials = createCredentialsMap(credentialAttributes);
  }

  protected Map<CredentialAttributes, String> getCredentials() {
    return credentials;
  }

  protected void setCredential(CredentialAttributes credentialAttributes, String value) {
    apply(Map.of(credentialAttributes, value));
  }

  protected String getCredential(CredentialAttributes credentialAttributes) {
    return credentials.get(credentialAttributes);
  }


  protected boolean isModified(Map<CredentialAttributes, String> credentials) {
    for (Entry<CredentialAttributes, String> entry : this.credentials.entrySet()) {
      if (!credentials.containsKey(entry.getKey()) || areValuesDifferent(
          entry.getValue(), credentials.get(entry.getKey()))) {
        return true;
      }
    }
    return false;
  }

  protected void apply(Map<CredentialAttributes, String> credentials) {
    for (Entry<CredentialAttributes, String> entry : credentials.entrySet()) {
      if (!this.credentials.containsKey(entry.getKey())) {
        throw new IllegalArgumentException(
            String.format("Credential key %s does not exist for %s", entry.getKey(),
                this.getClass().getSimpleName()));
      }
      CredentialsUtil.setPassword(entry.getKey(), entry.getValue());
      this.credentials.put(entry.getKey(), entry.getValue());
    }
  }

  private static Map<CredentialAttributes, String> createCredentialsMap(
      Set<CredentialAttributes> set) {
    return new HashMap<>(set.stream()
        .collect(Collectors.toMap(attribute -> attribute, attribute -> {
          String password = CredentialsUtil.getPassword(attribute);
          return password != null ? password : "";
        })));
  }

  public abstract boolean isCredentialSet();
}
