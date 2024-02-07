package ee.carlrobert.codegpt.settings.state.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.Credentials;

/**
 * Settings for using a remote service.
 */
public class RemoteSettings<T extends Credentials> extends CommonSettings<T> {

  protected String baseHost = "http://localhost:8080";
  protected String path = null;

  public RemoteSettings() {
  }

  public RemoteSettings(String baseHost, String path, T credentials) {
    this.baseHost = baseHost;
    this.path = path;
    this.credentials = credentials;
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

  @Transient
  public boolean isModified(RemoteSettings<T> remoteSettings) {
    return !StringUtil.equals(this.baseHost, remoteSettings.getBaseHost())
        || !StringUtil.equals(remoteSettings.getPath(), this.path);
  }

}
