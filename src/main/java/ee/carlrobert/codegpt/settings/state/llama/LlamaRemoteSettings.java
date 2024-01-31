package ee.carlrobert.codegpt.settings.state.llama;

import ee.carlrobert.codegpt.completions.PromptTemplate;

/**
 * All settings for using an existing remote Llama server
 */
public class LlamaRemoteSettings extends LlamaCommonSettings {

  private String baseHost = "http://localhost:8080";

  public LlamaRemoteSettings() {
  }

  public LlamaRemoteSettings(PromptTemplate promptTemplate, String baseHost) {
    super(promptTemplate);
    this.baseHost = baseHost;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public boolean isModified(LlamaRemoteSettings remoteSettings, String apiKey) {
    return super.isModified(remoteSettings, apiKey)
        || !baseHost.equals(remoteSettings.getBaseHost());
  }
}
