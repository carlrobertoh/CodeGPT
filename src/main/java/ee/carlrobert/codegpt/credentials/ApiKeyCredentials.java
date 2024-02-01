//package ee.carlrobert.codegpt.credentials;
//
//import static ee.carlrobert.codegpt.util.Utils.areStringsDifferentIgnoringEmptyOrNull;
//
//import com.intellij.credentialStore.CredentialAttributes;
//import java.util.Objects;
//
//public class ApiKeyCredentials extends Credentials {
//
//  protected String apiKey;
//
//  public ApiKeyCredentials(String apiKey) {
//    this.apiKey = apiKey;
//  }
//
//  ApiKeyCredentials() {
//  }
//
//  public String getApiKey() {
//    return apiKey;
//  }
//
//  public void setApiKey(String apiKey) {
//    this.apiKey = apiKey;
//  }
//
//  boolean isApiKeySet() {
//    return apiKey != null && !apiKey.isEmpty();
//  }
//
//  public boolean isModified(ApiKeyCredentials credentials) {
//    return areStringsDifferentIgnoringEmptyOrNull(credentials.getApiKey(), credentials.apiKey);
//  }
//
//}
