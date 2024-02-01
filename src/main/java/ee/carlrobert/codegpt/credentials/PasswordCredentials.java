//package ee.carlrobert.codegpt.credentials;
//
//import static ee.carlrobert.codegpt.util.Utils.areStringsDifferentIgnoringEmptyOrNull;
//
//public class PasswordCredentials extends Credentials {
//
//  protected String password;
//
//  public PasswordCredentials(String password) {
//    this.password = password;
//  }
//
//  PasswordCredentials() {
//  }
//
//  public String getPassword() {
//    return password;
//  }
//
//  public void setPassword(String password) {
//    this.password = password;
//  }
//
//  boolean isPasswordySet() {
//    return password != null && !password.isEmpty();
//  }
//
//  public boolean isModified(PasswordCredentials credentials) {
//    return areStringsDifferentIgnoringEmptyOrNull(credentials.getPassword(), credentials.password);
//  }
//
//}
