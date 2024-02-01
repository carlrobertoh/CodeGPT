package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class YouCredentialsManager extends CredentialsManager {

  private static final CredentialAttributes accountPasswordCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("ACCOUNT_PASSWORD");

  public YouCredentialsManager() {
    super(Set.of(accountPasswordCredentialAttributes));
    
  }
  
  public YouCredentialsManager(CredentialAttributes accountPasswordCredentialAttributes,
      Set<CredentialAttributes> additionalAttributes) {
    super(mergeAttributes(additionalAttributes, accountPasswordCredentialAttributes));
  }

  public static Set<CredentialAttributes> mergeAttributes(Set<CredentialAttributes> attributes,
      CredentialAttributes attribute) {
    Set<CredentialAttributes> merged = new HashSet<>(attributes);
    merged.add(attribute);
    return merged;
  }

  public String getPassword() {
    return getCredential(accountPasswordCredentialAttributes);
  }

  public void setPassword(String password) {
    setCredential(accountPasswordCredentialAttributes, password);
  }

  @Override
  public boolean isCredentialSet() {
    String password = getPassword();
    return password != null && !password.isEmpty();
  }

  public boolean isModified(String password) {
    return super.isModified(Map.of(accountPasswordCredentialAttributes, password));
  }

}
