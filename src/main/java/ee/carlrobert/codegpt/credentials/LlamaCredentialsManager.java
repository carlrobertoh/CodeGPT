package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.Nullable;

@Service
public final class LlamaCredentialsManager {

  private static final CredentialAttributes llamaApiKeyCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("LLAMA_API_KEY");

  private String llamaApiKey;

  private LlamaCredentialsManager() {
    llamaApiKey = CredentialsUtil.getPassword(llamaApiKeyCredentialAttributes);
  }

  public static LlamaCredentialsManager getInstance() {
    return ApplicationManager.getApplication().getService(LlamaCredentialsManager.class);
  }

  public boolean isApiKeySet() {
    return llamaApiKey != null && !llamaApiKey.isEmpty();
  }

  public @Nullable String getApiKey() {
    return llamaApiKey;
  }

  public void setApiKey(String llamaApiKey) {
    this.llamaApiKey = llamaApiKey;
    CredentialsUtil.setPassword(llamaApiKeyCredentialAttributes, llamaApiKey);
  }
}
