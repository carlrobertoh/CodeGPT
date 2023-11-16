package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.Nullable;

@Service
public final class OpenAICredentialsManager {

  private static final CredentialAttributes openAIApiKeyCredentialAttributes =
      CredentialsUtil.createCredentialAttributes("OPENAI_API_KEY");

  private String openAIApiKey;

  private OpenAICredentialsManager() {
    openAIApiKey = CredentialsUtil.getPassword(openAIApiKeyCredentialAttributes);
  }

  public static OpenAICredentialsManager getInstance() {
    return ApplicationManager.getApplication().getService(OpenAICredentialsManager.class);
  }

  public boolean isApiKeySet() {
    return openAIApiKey != null && !openAIApiKey.isEmpty();
  }

  public @Nullable String getApiKey() {
    return openAIApiKey;
  }

  public void setApiKey(String openAIApiKey) {
    this.openAIApiKey = openAIApiKey;
    CredentialsUtil.setPassword(openAIApiKeyCredentialAttributes, openAIApiKey);
  }
}
