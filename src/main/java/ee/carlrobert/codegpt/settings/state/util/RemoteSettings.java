package ee.carlrobert.codegpt.settings.state.util;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import ee.carlrobert.codegpt.credentials.CredentialsManager;

/**
 * Settings for using a remote service
 */
public class RemoteSettings<T extends CredentialsManager> extends CommonSettings<T> {

  protected String baseHost = "http://localhost:8080";
  protected String path = null;

  public RemoteSettings() {
  }

  public RemoteSettings(String baseHost, String path, T credentialsManager) {
    this.baseHost = baseHost;
    this.path = path;
    this.credentialsManager = credentialsManager;
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

  public boolean isModified(RemoteSettings<T> remoteSettings) {
    return super.isModified(remoteSettings)
        || !remoteSettings.getBaseHost().equals(this.baseHost)
        || areValuesDifferent(remoteSettings.getPath(), this.path);
  }

}
