package ee.carlrobert.codegpt.settings.service.you;

import ee.carlrobert.llm.client.you.completion.YouCompletionCustomModel;
import ee.carlrobert.llm.client.you.completion.YouCompletionMode;
import java.util.Objects;

public class YouSettingsState {

  private String email = "";
  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;
  private YouCompletionMode chatMode;
  private YouCompletionCustomModel customModel;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public YouCompletionMode getChatMode() {
    return chatMode;
  }

  public void setChatMode(YouCompletionMode chatMode) {
    this.chatMode = chatMode;
  }

  public YouCompletionCustomModel getCustomModel() {
    return customModel;
  }

  public void setCustomModel(YouCompletionCustomModel customModel) {
    this.customModel = customModel;
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
        && useGPT4Model == that.useGPT4Model
        && Objects.equals(email, that.email)
        && chatMode == that.chatMode
        && customModel == that.customModel;
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayWebSearchResults, useGPT4Model, email, chatMode, customModel);
  }
}
