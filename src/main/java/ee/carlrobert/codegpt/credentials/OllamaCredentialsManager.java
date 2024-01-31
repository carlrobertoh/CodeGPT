package ee.carlrobert.codegpt.credentials;

import org.jetbrains.annotations.Nullable;

public final class OllamaCredentialsManager extends ServiceCredentialsManager {

  public OllamaCredentialsManager() {
    super(null);
  }

  @Override
  public boolean providesApiKey() {
    return false;
  }

  @Override
  public @Nullable String getApiKey() {
    throw new UnsupportedOperationException("Ollama Server does not provide option to authenticate via apiKey!");
  }

  @Override
  public void setApiKey(String apiKey) {
    throw new UnsupportedOperationException("Ollama Server does not provide option to authenticate via apiKey!");
  }
}
