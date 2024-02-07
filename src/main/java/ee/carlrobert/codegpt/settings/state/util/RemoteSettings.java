package ee.carlrobert.codegpt.settings.state.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Transient;

/**
 * Settings for using a remote service.
 */
public class RemoteSettings {

  protected String baseHost = "http://localhost:8080";
  protected String path;

  public RemoteSettings() {
  }

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

  @Transient
  public boolean isModified(RemoteSettings remoteSettings) {
    return !StringUtil.equals(this.baseHost, remoteSettings.getBaseHost())
        || !StringUtil.equals(remoteSettings.getPath(), this.path);
  }

}
