package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.credentials.ServiceCredentialsManager;

/**
 * All settings that both {@link LlamaLocalSettings} and {@link LlamaRemoteSettings} need.
 */
public class LlamaCommonSettings {

  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;

  @Transient
  protected ServiceCredentialsManager credentialsManager;

  public LlamaCommonSettings() {
  }

  public LlamaCommonSettings(ServiceCredentialsManager credentialsManager) {
    this.credentialsManager = credentialsManager;
  }

  public LlamaCommonSettings(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public boolean isModified(LlamaCommonSettings llamaCommonSettings, String apikey) {
    return !promptTemplate.equals(llamaCommonSettings.getPromptTemplate())
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
