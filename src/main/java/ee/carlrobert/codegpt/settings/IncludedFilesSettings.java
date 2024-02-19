package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(
    name = "CodeGPT_IncludedFilesSettings",
    storages = @Storage("CodeGPT_IncludedFilesSettings.xml"))
public class IncludedFilesSettings implements PersistentStateComponent<IncludedFilesSettingsState> {

  private IncludedFilesSettingsState state = new IncludedFilesSettingsState();

  @Override
  @NotNull
  public IncludedFilesSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull IncludedFilesSettingsState state) {
    this.state = state;
  }

  public static IncludedFilesSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static IncludedFilesSettings getInstance() {
    return ApplicationManager.getApplication().getService(IncludedFilesSettings.class);
  }
}
