package ee.carlrobert.codegpt.settings.state.you;

import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;

public class YouSettingsState extends CommonSettings<PasswordCredentials> {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;

  public YouSettingsState() {
    this.credentials = new PasswordCredentials();
  }

  public YouSettingsState(boolean displayWebSearchResults, boolean useGPT4Model,
      PasswordCredentials credentials) {
    this.displayWebSearchResults = displayWebSearchResults;
    this.useGPT4Model = useGPT4Model;
    this.credentials = credentials;
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResults;
  }

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    this.displayWebSearchResults = displayWebSearchResults;
  }

  public boolean isUseGPT4Model() {
    return useGPT4Model;
  }

  public void setUseGPT4Model(boolean useGPT4Model) {
    this.useGPT4Model = useGPT4Model;
  }
}
