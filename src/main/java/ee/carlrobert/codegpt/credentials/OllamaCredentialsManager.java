package ee.carlrobert.codegpt.credentials;

public class OllamaCredentialsManager extends LlamaCredentialsManager {

  public OllamaCredentialsManager(String suffix) {
    super(suffix);
  }

  @Override
  public String getApiKey() {
    return null;
  }

  @Override
  public void apply(String apiKey) {

  }

  @Override
  public boolean isCredentialSet() {
    return true;
  }

  @Override
  public boolean isModified(String apiKey) {
    return false;
  }

  @Override
  public boolean isActive() {
    return false;
  }
}
