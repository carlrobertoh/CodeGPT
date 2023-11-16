package ee.carlrobert.codegpt.settings.advanced;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.net.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "CodeGPT_AdvancedSettings_210",
    storages = @Storage("CodeGPT_AdvancedSettings_210.xml"))
public class AdvancedSettingsState implements PersistentStateComponent<AdvancedSettingsState> {

  private String proxyHost = "";
  private int proxyPort;
  private Proxy.Type proxyType = Proxy.Type.SOCKS;
  private boolean proxyAuthSelected;
  private String proxyUsername;
  private String proxyPassword;
  private int connectTimeout = 30;
  private int readTimeout = 30;

  public static AdvancedSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(AdvancedSettingsState.class);
  }

  @Nullable
  @Override
  public AdvancedSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AdvancedSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  public Proxy.Type getProxyType() {
    return proxyType;
  }

  public void setProxyType(Proxy.Type proxyType) {
    this.proxyType = proxyType;
  }

  public boolean isProxyAuthSelected() {
    return proxyAuthSelected;
  }

  public void setProxyAuthSelected(boolean proxyAuthSelected) {
    this.proxyAuthSelected = proxyAuthSelected;
  }

  public String getProxyUsername() {
    return proxyUsername;
  }

  public void setProxyUsername(String proxyUsername) {
    this.proxyUsername = proxyUsername;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }
}
