//package ee.carlrobert.codegpt.credentials;
//
//import static ee.carlrobert.codegpt.util.Utils.areStringsDifferentIgnoringEmptyOrNull;
//
//import com.intellij.credentialStore.CredentialAttributes;
//import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
//import java.util.Objects;
//
//public class AzureCredentials extends ApiKeyCredentials {
//
//  protected String activeDirectoryToken;
//
//  public AzureCredentials(String apiKey, String activeDirectoryToken) {
//    super(apiKey);
//    this.activeDirectoryToken = activeDirectoryToken;
//  }
//
//  AzureCredentials() {
//  }
//
//  public String getActiveDirectoryToken() {
//    return activeDirectoryToken;
//  }
//
//  public void setActiveDirectoryToken(String activeDirectoryToken) {
//    this.activeDirectoryToken = activeDirectoryToken;
//  }
//
//  public boolean isModified(AzureCredentials credentials) {
//    return super.isModified(credentials)
//        || areStringsDifferentIgnoringEmptyOrNull(credentials.getApiKey(), credentials.apiKey);
//  }
//
//  boolean isTokenSet() {
//    return activeDirectoryToken != null && !activeDirectoryToken.isEmpty();
//  }
//
//  public String getSecret() {
//    return AzureSettingsState.getInstance().isUseAzureActiveDirectoryAuthentication()
//        ? activeDirectoryToken
//        : apiKey;
//  }
//
//}
