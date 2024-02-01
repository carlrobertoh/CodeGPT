package ee.carlrobert.codegpt.settings.state.llama;

import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.RemoteSettings;

/**
 * All settings for using an existing remote Llama server
 */
public class LlamaRemoteSettings extends RemoteSettings<LlamaCredentialsManager> {

  private PromptTemplate promptTemplate;

  public LlamaRemoteSettings(PromptTemplate promptTemplate, String baseHost) {
    super(baseHost, null);
    this.promptTemplate = promptTemplate;
  }

  public boolean isModified(LlamaRemoteSettings llamaCommonSettings) {
    return super.isModified(llamaCommonSettings)
        || !promptTemplate.equals(llamaCommonSettings.getPromptTemplate());
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

}
