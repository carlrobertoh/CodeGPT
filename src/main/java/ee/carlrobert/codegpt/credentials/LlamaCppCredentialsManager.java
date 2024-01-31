package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import org.jetbrains.annotations.Nullable;

public final class LlamaCppCredentialsManager extends ServiceCredentialsManager {

  private final CredentialAttributes llamaApiKeyCredentialAttributes;

  private String llamaApiKey;

  public LlamaCppCredentialsManager(String suffix) {
    super(suffix);
    llamaApiKeyCredentialAttributes = CredentialsUtil.createCredentialAttributes("LLAMA_API_KEY" + suffix);
    llamaApiKey = CredentialsUtil.getPassword(llamaApiKeyCredentialAttributes);
  }

  @Override
  public @Nullable String getApiKey() {
    return llamaApiKey;
  }

  @Override
  public void setApiKey(String llamaApiKey) {
    this.llamaApiKey = llamaApiKey;
    CredentialsUtil.setPassword(llamaApiKeyCredentialAttributes, llamaApiKey);
  }
}
