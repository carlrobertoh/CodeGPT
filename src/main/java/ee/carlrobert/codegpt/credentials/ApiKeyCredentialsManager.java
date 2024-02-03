package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ApiKeyCredentialsManager extends CredentialsManager {

  protected CredentialAttributes apiKeyAttribute;

  public ApiKeyCredentialsManager(CredentialAttributes apiKeyAttribute) {
    super(Set.of(apiKeyAttribute));
    this.apiKeyAttribute = apiKeyAttribute;
  }

  public ApiKeyCredentialsManager(CredentialAttributes apiKeyAttribute,
      Set<CredentialAttributes> additionalAttributes) {
    super(mergeAttributes(additionalAttributes, apiKeyAttribute));
    this.apiKeyAttribute = apiKeyAttribute;
  }

  public static Set<CredentialAttributes> mergeAttributes(Set<CredentialAttributes> attributes,
      CredentialAttributes attribute) {
    Set<CredentialAttributes> merged = new HashSet<>(attributes);
    merged.add(attribute);
    return merged;
  }

  public String getApiKey() {
    return getCredential(apiKeyAttribute);
  }

  public void apply(String apiKey) {
    setCredential(apiKeyAttribute, apiKey);
  }

  @Override
  public boolean isCredentialSet() {
    String apiKey = getApiKey();
    return apiKey != null && !apiKey.isEmpty();
  }

  public boolean isModified(String apiKey) {
    return super.isModified(Map.of(apiKeyAttribute, apiKey));
  }
}
