package ee.carlrobert.codegpt.credentials.manager;

import ee.carlrobert.codegpt.credentials.Credentials;

public abstract class CredentialsManager<T extends Credentials> {

  protected T credentials;

  public CredentialsManager() {
  }

  public void setCredentials(T credentials) {
    this.credentials = credentials;
  }

  public T getCredentials() {
    return credentials;
  }

  public abstract void apply(T credentials);

}
