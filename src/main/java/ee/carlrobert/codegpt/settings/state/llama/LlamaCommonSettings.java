package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.CommonSettings;

/**
 * All settings that both {@link LlamaLocalSettings} and {@link LlamaRemoteSettings} need.
 */
public class LlamaCommonSettings extends CommonSettings<LlamaCredentialsManager> {

  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;

  public LlamaCommonSettings() {
  }

  public LlamaCommonSettings(LlamaCredentialsManager credentialsManager) {
    this.credentialsManager = credentialsManager;
  }

  public LlamaCommonSettings(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  protected boolean isModified(LlamaCommonSettings llamaCommonSettings) {
    return !promptTemplate.equals(llamaCommonSettings.getPromptTemplate());
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

}
