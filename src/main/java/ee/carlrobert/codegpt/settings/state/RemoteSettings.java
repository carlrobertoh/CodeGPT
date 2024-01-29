package ee.carlrobert.codegpt.settings.state;

import ee.carlrobert.codegpt.completions.PromptTemplate;

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

  public boolean isModified(RemoteSettings remoteSettings) {
    return super.isModified(remoteSettings)
        || !baseHost.equals(remoteSettings.getBaseHost());
  }
}
