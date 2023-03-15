package ee.carlrobert.codegpt.ide.settings.advanced;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AdvancedSettingsConfigurable implements Configurable {

  private AdvancedSettingsComponent advancedSettingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeGPT: Advanced Settings";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    advancedSettingsComponent = new AdvancedSettingsComponent(advancedSettings);
    return advancedSettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    return !advancedSettingsComponent.getProxyType().equals(advancedSettings.proxyType) ||
        !advancedSettingsComponent.getProxyHost().equals(advancedSettings.proxyHost) ||
        advancedSettingsComponent.getProxyPort() != advancedSettings.proxyPort ||
        advancedSettingsComponent.isProxyAuthSelected() != advancedSettings.isProxyAuthSelected ||
        !advancedSettingsComponent.getProxyAuthUsername().equals(advancedSettings.proxyUsername) ||
        !advancedSettingsComponent.getProxyAuthPassword().equals(advancedSettings.proxyPassword);
  }

  @Override
  public void apply() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    advancedSettings.proxyType = advancedSettingsComponent.getProxyType();
    advancedSettings.proxyHost = advancedSettingsComponent.getProxyHost();
    advancedSettings.proxyPort = advancedSettingsComponent.getProxyPort();
    advancedSettings.isProxyAuthSelected = advancedSettingsComponent.isProxyAuthSelected();
    advancedSettings.proxyUsername = advancedSettingsComponent.getProxyAuthUsername();
    advancedSettings.proxyPassword = advancedSettingsComponent.getProxyAuthPassword();
  }

  @Override
  public void reset() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    advancedSettingsComponent.setProxyType(advancedSettings.proxyType);
    advancedSettingsComponent.setProxyHost(advancedSettings.proxyHost);
    advancedSettingsComponent.setProxyPort(advancedSettings.proxyPort);
    advancedSettingsComponent.setUseProxyAuthentication(advancedSettings.isProxyAuthSelected);
    advancedSettingsComponent.setProxyUsername(advancedSettings.proxyUsername);
    advancedSettingsComponent.setProxyPassword(advancedSettings.proxyPassword);
  }

  @Override
  public void disposeUIResources() {
    advancedSettingsComponent = null;
  }
}
