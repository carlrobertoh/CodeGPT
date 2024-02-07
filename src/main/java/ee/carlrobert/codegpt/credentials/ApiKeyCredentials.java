package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.util.text.StringUtil;

public class ApiKeyCredentials implements Credentials {

  private String apiKey;

  public ApiKeyCredentials() {
  }

  public ApiKeyCredentials(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public boolean isCredentialSet() {
    return apiKey != null && !apiKey.isEmpty();
  }

  public boolean isModified(ApiKeyCredentials apiKeyCredentials) {
    return !StringUtil.equals(apiKey, apiKeyCredentials.getApiKey());
  }
}
