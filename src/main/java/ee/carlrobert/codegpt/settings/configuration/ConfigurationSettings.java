package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(
    name = "CodeGPT_ConfigurationSettings_210",
    storages = @Storage("CodeGPT_ConfigurationSettings_210.xml"))
public class ConfigurationSettings implements PersistentStateComponent<ConfigurationState> {

  private ConfigurationState state = new ConfigurationState();

  @Override
  @NotNull
  public ConfigurationState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull ConfigurationState state) {
    this.state = state;
  }

  public static ConfigurationState getCurrentState() {
    return getInstance().getState();
  }

  public static ConfigurationSettings getInstance() {
    return ApplicationManager.getApplication().getService(ConfigurationSettings.class);
  }

  public static String getSystemPrompt() {
    return getCurrentState().getSystemPrompt();
  }
}
