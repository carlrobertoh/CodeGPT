package ee.carlrobert.codegpt.settings.service.watsonx;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_WatsonxSettings", storages = @Storage("CodeGPT_WatsonxSettings.xml"))
public class WatsonxSettings implements PersistentStateComponent<WatsonxSettingsState> {

  private WatsonxSettingsState state = new WatsonxSettingsState();

  public static WatsonxSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static boolean isCodeCompletionsPossible() {
    return getInstance().getState().isCodeCompletionsEnabled();
  }

  public static ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettings getInstance() {
    return ApplicationManager.getApplication().getService(WatsonxSettings.class);
  }

  @Override
  @NotNull
  public WatsonxSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull WatsonxSettingsState state) {
    this.state = state;
  }
}
