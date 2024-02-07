package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.util.text.StringUtil;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import org.apache.commons.lang.StringUtils;

public class AzureCredentials extends ApiKeyCredentials {

  private String activeDirectoryToken;

  public AzureCredentials() {
  }

  public AzureCredentials(String apiKey, String activeDirectoryToken) {
    super(apiKey);
    this.activeDirectoryToken = activeDirectoryToken;
  }

  public String getActiveDirectoryToken() {
    return activeDirectoryToken;
  }

  public void setActiveDirectoryToken(
      String activeDirectoryTokenCredentialAttributes) {
    this.activeDirectoryToken = activeDirectoryTokenCredentialAttributes;
  }


  public String getSecret() {
    return AzureSettings.getInstance().getState().isUseAzureActiveDirectoryAuthentication()
        ? getActiveDirectoryToken()
        : getApiKey();
  }

  @Override
  public boolean isCredentialSet() {
    if (AzureSettings.getInstance().getState().isUseAzureApiKeyAuthentication()) {
      return super.isCredentialSet();
    }
    return activeDirectoryToken != null && !activeDirectoryToken.isEmpty();
  }

  public boolean isModified(AzureCredentials azureCredentials) {
    return super.isModified(azureCredentials)
        || !StringUtil.equals(StringUtils.defaultString(activeDirectoryToken),
        azureCredentials.getActiveDirectoryToken());
  }

}
