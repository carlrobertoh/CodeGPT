package ee.carlrobert.codegpt.credentials;

abstract class SingleCredentialManager extends AbstractCredentialManager {

  private final String key;

  public SingleCredentialManager(String key) {
    super(key);
    this.key = key;
  }

  @Override
  public boolean isCredentialSet() {
    return isCredentialSet(key);
  }

  @Override
  public String getCredential() {
    return getCredential(key);
  }

  public void setCredential(String credential) {
    setCredential(key, credential);
  }
}
