package ee.carlrobert.codegpt.settings.state.util;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.CredentialsManager;

/**
 * Settings that all service settings share in common.
 */
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

  @Transient
  public boolean isModified(CommonSettings<T> commonSettings) {
    return false;
  }
}
