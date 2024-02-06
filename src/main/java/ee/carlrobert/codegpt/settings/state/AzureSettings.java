package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.manager.AzureCredentialsManager;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettings implements PersistentStateComponent<AzureSettingsState> {

  private final AzureCredentialsManager credentialsManager;
  private AzureSettingsState state;

  public AzureSettings() {
    this.state = new AzureSettingsState();
    this.credentialsManager = AzureCredentialsManager.getInstance();
  }

  @NonInjectable
  public AzureSettings(AzureCredentialsManager credentialsManager) {
    this.state = new AzureSettingsState();
    this.credentialsManager = credentialsManager;
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
    this.state.setCredentials(credentialsManager.getCredentials());
  }

  public void apply(AzureSettingsState settingsState) {
    this.state = settingsState;
    credentialsManager.apply(settingsState.getCredentials());
  }
}
