package ee.carlrobert.codegpt.settings.state;

import ee.carlrobert.codegpt.credentials.CredentialsManager;

public class RemoteSettings<T extends CredentialsManager> extends CommonSettings<T> {

  protected String baseHost;
  protected String path;

  public RemoteSettings(String baseHost, String path) {
    this.baseHost = baseHost;
    this.path = path;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public boolean isModified(RemoteSettings<?> remoteSettings) {
    return !remoteSettings.getBaseHost().equals(this.baseHost)
        || !remoteSettings.getPath().equals(this.path);
  }

}
