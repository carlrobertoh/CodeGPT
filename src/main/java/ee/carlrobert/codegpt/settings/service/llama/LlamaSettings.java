package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT", storages = @Storage("CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private LlamaSettingsState state = new LlamaSettingsState();

  @Override
  @NotNull
  public LlamaSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    this.state = state;
  }

  public static LlamaSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettings.class);
  }
}
