package ee.carlrobert.codegpt.credentials.manager;

import ee.carlrobert.codegpt.credentials.Credentials;
import ee.carlrobert.codegpt.credentials.CredentialsService;

public abstract class CredentialsManager<T extends Credentials> {

  protected final CredentialsService credentialsService;
  protected T credentials;

  public CredentialsManager() {
    this.credentialsService = CredentialsService.getInstance();
  }

  public CredentialsManager(CredentialsService credentialsService) {
    this.credentialsService = credentialsService;
  }

  public void setCredentials(T credentials) {
    this.credentials = credentials;
  }

  public T getCredentials() {
    return credentials;
  }

  public abstract void apply(T credentials);

}
