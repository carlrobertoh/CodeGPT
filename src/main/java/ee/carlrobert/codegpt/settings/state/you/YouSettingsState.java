package ee.carlrobert.codegpt.settings.state.you;


public class YouSettingsState {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;

  public YouSettingsState() {
  }

  public YouSettingsState(boolean displayWebSearchResults, boolean useGPT4Model) {
    this.displayWebSearchResults = displayWebSearchResults;
    this.useGPT4Model = useGPT4Model;
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
