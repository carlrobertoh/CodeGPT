package ee.carlrobert.codegpt.settings.advanced;

import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AdvancedSettingsConfigurable implements Configurable {

  private AdvancedSettingsComponent advancedSettingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("advancedSettingsConfigurable.displayName");
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
    return !advancedSettingsComponent.getProxyType().equals(advancedSettings.getProxyType())
        || !advancedSettingsComponent.getProxyHost().equals(advancedSettings.getProxyHost())
        || advancedSettingsComponent.getProxyPort() != advancedSettings.getProxyPort()
        || advancedSettingsComponent.isProxyAuthSelected() != advancedSettings.isProxyAuthSelected()
        || !advancedSettingsComponent.getProxyAuthUsername()
        .equals(advancedSettings.getProxyUsername())
        || !advancedSettingsComponent.getProxyAuthPassword()
        .equals(advancedSettings.getProxyPassword())
        || advancedSettingsComponent.getConnectionTimeout() != advancedSettings.getConnectTimeout()
        || advancedSettingsComponent.getReadTimeout() != advancedSettings.getReadTimeout();
  }

  @Override
  public void apply() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    advancedSettings.setProxyType(advancedSettingsComponent.getProxyType());
    advancedSettings.setProxyHost(advancedSettingsComponent.getProxyHost());
    advancedSettings.setProxyPort(advancedSettingsComponent.getProxyPort());
    advancedSettings.setProxyAuthSelected(advancedSettingsComponent.isProxyAuthSelected());
    advancedSettings.setProxyUsername(advancedSettingsComponent.getProxyAuthUsername());
    advancedSettings.setProxyPassword(advancedSettingsComponent.getProxyAuthPassword());
    advancedSettings.setConnectTimeout(advancedSettingsComponent.getConnectionTimeout());
    advancedSettings.setReadTimeout(advancedSettingsComponent.getReadTimeout());
  }

  @Override
  public void reset() {
    var advancedSettings = AdvancedSettingsState.getInstance();
    advancedSettingsComponent.setProxyType(advancedSettings.getProxyType());
    advancedSettingsComponent.setProxyHost(advancedSettings.getProxyHost());
    advancedSettingsComponent.setProxyPort(advancedSettings.getProxyPort());
    advancedSettingsComponent.setUseProxyAuthentication(advancedSettings.isProxyAuthSelected());
    advancedSettingsComponent.setProxyUsername(advancedSettings.getProxyUsername());
    advancedSettingsComponent.setProxyPassword(advancedSettings.getProxyPassword());
    advancedSettingsComponent.setConnectionTimeoutField(advancedSettings.getConnectTimeout());
    advancedSettingsComponent.setReadTimeout(advancedSettings.getReadTimeout());
  }

  @Override
  public void disposeUIResources() {
    advancedSettingsComponent = null;
  }
}
