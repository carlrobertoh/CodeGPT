package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.credentials.ServiceCredentialsManager;

public class CommonSettings {

  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;

  @Transient
  protected ServiceCredentialsManager credentialsManager;

  public CommonSettings() {
  }

  public CommonSettings(ServiceCredentialsManager credentialsManager) {
    this.credentialsManager = credentialsManager;
  }

  public CommonSettings(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public boolean isModified(CommonSettings commonSettings, String apikey) {
    return !promptTemplate.equals(commonSettings.getPromptTemplate())
        || credentialsManager.isModified(apikey);
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  @Transient
  public ServiceCredentialsManager getCredentialsManager() {
    return credentialsManager;
  }

  public void setCredentialsManager(
      ServiceCredentialsManager credentialsManager) {
    this.credentialsManager = credentialsManager;
  }
}
