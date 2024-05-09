package ee.carlrobert.codegpt.settings.service.azure;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettings implements PersistentStateComponent<AzureSettingsState> {

  private AzureSettingsState state = new AzureSettingsState();

  @Override
  @NotNull
  public AzureSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    this.state = state;
  }

  public static AzureSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static AzureSettings getInstance() {
    return ApplicationManager.getApplication().getService(AzureSettings.class);
  }
}
