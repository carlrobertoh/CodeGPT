package ee.carlrobert.codegpt.settings.service.you;

import java.util.Objects;

public class YouSettingsState {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    YouSettingsState that = (YouSettingsState) o;
    return displayWebSearchResults == that.displayWebSearchResults
        && useGPT4Model == that.useGPT4Model;
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayWebSearchResults, useGPT4Model);
  }
}
