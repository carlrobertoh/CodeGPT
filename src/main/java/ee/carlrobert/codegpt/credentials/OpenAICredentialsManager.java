package ee.carlrobert.codegpt.credentials;

import com.intellij.credentialStore.CredentialAttributes;
import org.jetbrains.annotations.Nullable;

public class OpenAICredentialsManager {

  private static final CredentialAttributes openAIApiKeyCredentialAttributes = CredentialsUtil.createCredentialAttributes("OPENAI_API_KEY");
  private static OpenAICredentialsManager instance;

  private String openAIApiKey;

  private OpenAICredentialsManager() {
    openAIApiKey = CredentialsUtil.getPassword(openAIApiKeyCredentialAttributes);
  }

  public static OpenAICredentialsManager getInstance() {
    if (instance == null) {
      instance = new OpenAICredentialsManager();
    }
    return instance;
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
