package ee.carlrobert.chatgpt.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable implements Configurable {

  private SettingsComponent settingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "ChatGPT: Settings";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return settingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    settingsComponent = new SettingsComponent();
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    SettingsState settings = SettingsState.getInstance();
    return !settingsComponent.getApiKeyField().equals(settings.secretKey);
  }

  @Override
  public void apply() {
    SettingsState settings = SettingsState.getInstance();
    settings.secretKey = settingsComponent.getApiKeyField();
  }

  @Override
  public void reset() {
    SettingsState settings = SettingsState.getInstance();
    settingsComponent.setApiKeyField(settings.secretKey);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }
}
