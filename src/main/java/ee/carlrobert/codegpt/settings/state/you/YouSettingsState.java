package ee.carlrobert.codegpt.settings.state.you;

import ee.carlrobert.codegpt.credentials.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;

public class YouSettingsState extends RemoteSettings<YouCredentialsManager> {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;

  public YouSettingsState() {
    super(null, null, new YouCredentialsManager());
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
