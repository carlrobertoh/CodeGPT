package ee.carlrobert.chatgpt.ide.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.chatgpt.ide.notification.NotificationService;
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
    var settings = SettingsState.getInstance();
    return !settingsComponent.getApiKeyField().equals(settings.secretKey);
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();
    settings.secretKey = settingsComponent.getApiKeyField();
    if (!settings.secretKey.isEmpty()) {
      ApplicationManager.getApplication().getService(NotificationService.class).expire();
    }
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    settingsComponent.setApiKeyField(settings.secretKey);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }
}
