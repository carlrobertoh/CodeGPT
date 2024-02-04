package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.manager.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettings implements PersistentStateComponent<AzureSettingsState> {

  private AzureSettingsState state;

  public AzureSettings() {
    this.state = new AzureSettingsState();
  }

  public static AzureSettings getInstance() {
    return ApplicationManager.getApplication()
        .getService(AzureSettings.class);
  }

  @Override
  @NotNull
  public AzureSettingsState getState() {
    return this.state;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
    this.state.setCredentials(AzureCredentialsManager.getInstance().getCredentials());
  }

  public void apply(AzureSettingsState settingsState) {
    this.state = settingsState;
    AzureCredentialsManager.getInstance().apply(settingsState.getCredentials());
  }
}
