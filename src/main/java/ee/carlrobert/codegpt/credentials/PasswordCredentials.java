package ee.carlrobert.codegpt.credentials;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

public class PasswordCredentials implements Credentials {

  private String password;

  public PasswordCredentials() {
  }

  public PasswordCredentials(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean isCredentialSet() {
    return password != null && !password.isEmpty();
  }

  public boolean isModified(PasswordCredentials credentials) {
    return areValuesDifferent(password, credentials.getPassword());
  }
}
