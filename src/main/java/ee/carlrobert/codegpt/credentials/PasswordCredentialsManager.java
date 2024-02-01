//package ee.carlrobert.codegpt.credentials;
//
//import static ee.carlrobert.codegpt.util.Utils.areStringsDifferentIgnoringEmptyOrNull;
//
//import com.intellij.credentialStore.CredentialAttributes;
//
//public abstract class PasswordCredentialsManager<T extends PasswordCredentials> extends
//    CredentialsManager<T> {
//
//  protected CredentialAttributes passwordAttribute;
//
//  public PasswordCredentialsManager(T credentials, CredentialAttributes passwordAttribute) {
//    super(credentials);
//    this.passwordAttribute = passwordAttribute;
//    update();
//  }
//
//  public boolean providesApiKey() {
//    return true;
//  }
//
//  @Override
//  protected void update(){
//    credentials.setPassword(CredentialsUtil.getPassword(passwordAttribute));
//  }
//
//  @Override
//  public boolean isModified(T credentials) {
//    if (!providesApiKey()) {
//      return false;
//    }
//    return areStringsDifferentIgnoringEmptyOrNull(this.credentials.getPassword(),
//        credentials.getPassword());
//  }
//
//  @Override
//  public void apply(T credentials) {
//    CredentialsUtil.setPassword(passwordAttribute, credentials.getPassword());
//    update();
//  }
//
//  @Override
//  public boolean isCredentialSet() {
//    return credentials.isPasswordySet();
//  }
//}
