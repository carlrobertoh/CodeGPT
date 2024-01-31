package ee.carlrobert.codegpt.settings.state.llama;

import ee.carlrobert.codegpt.completions.PromptTemplate;

/**
 * All settings for using an existing remote server
 */
public class RemoteSettings extends CommonSettings {

  private String baseHost = "http://localhost:8080";

  public RemoteSettings() {
  }

  public RemoteSettings(PromptTemplate promptTemplate, String baseHost) {
    super(promptTemplate);
    this.baseHost = baseHost;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public boolean isModified(RemoteSettings remoteSettings, String apiKey) {
    return super.isModified(remoteSettings, apiKey)
        || !baseHost.equals(remoteSettings.getBaseHost());
  }
}
