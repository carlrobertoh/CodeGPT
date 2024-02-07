package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.util.text.StringUtil;

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
    return !StringUtil.equals(password, credentials.getPassword());
  }
}
