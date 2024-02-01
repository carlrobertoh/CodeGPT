package ee.carlrobert.codegpt.settings.state;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.CredentialsManager;

public class CommonSettings<T extends CredentialsManager> {

  @Transient
  protected T credentialsManager;

  public CommonSettings() {
  }

  @Transient
  public T getCredentialsManager() {
    return credentialsManager;
  }

  public void setCredentialsManager(
      T credentialsManager) {
    this.credentialsManager = credentialsManager;
  }
}
