package ee.carlrobert.codegpt.settings.state.llama;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;

/**
 * All settings for using an existing remote Llama server.
 */
public class LlamaRemoteSettings extends RemoteSettings<LlamaCredentialsManager> {

  public static final String CREDENTIALS_PREFIX = "REMOTE";

  private PromptTemplate chatPromptTemplate = PromptTemplate.LLAMA;
  private InfillPromptTemplate infillPromptTemplate = InfillPromptTemplate.LLAMA;

  public LlamaRemoteSettings() {
    super("http://localhost:8080", null, new LlamaCredentialsManager(CREDENTIALS_PREFIX));
  }

  public LlamaRemoteSettings(PromptTemplate chatPromptTemplate,
      InfillPromptTemplate infillPromptTemplate, String baseHost,
      LlamaCredentialsManager credentialsManager) {
    super(baseHost, null, credentialsManager);
    this.chatPromptTemplate = chatPromptTemplate;
    this.infillPromptTemplate = infillPromptTemplate;
  }

  @Transient
  public boolean isModified(LlamaRemoteSettings llamaRemoteSettings) {
    return super.isModified(llamaRemoteSettings)
        || !infillPromptTemplate.equals(llamaRemoteSettings.getInfillPromptTemplate())
        || !chatPromptTemplate.equals(llamaRemoteSettings.getChatPromptTemplate());
  }

  public PromptTemplate getChatPromptTemplate() {
    return chatPromptTemplate;
  }

  public void setChatPromptTemplate(PromptTemplate promptTemplate) {
    this.chatPromptTemplate = promptTemplate;
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return infillPromptTemplate;
  }

  public void setInfillPromptTemplate(
      InfillPromptTemplate infillPromptTemplate) {
    this.infillPromptTemplate = infillPromptTemplate;
  }
}
