package ee.carlrobert.codegpt.settings.advanced;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(
    name = "CodeGPT_AdvancedSettings_210",
    storages = @Storage("CodeGPT_AdvancedSettings_210.xml"))
public class AdvancedSettings implements PersistentStateComponent<AdvancedSettingsState> {

  private AdvancedSettingsState state = new AdvancedSettingsState();

  @Override
  @NotNull
  public AdvancedSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull AdvancedSettingsState state) {
    this.state = state;
  }

  public static AdvancedSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static AdvancedSettings getInstance() {
    return ApplicationManager.getApplication().getService(AdvancedSettings.class);
  }
}
