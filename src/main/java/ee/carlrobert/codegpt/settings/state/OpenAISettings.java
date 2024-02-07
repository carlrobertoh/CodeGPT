package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettings implements PersistentStateComponent<OpenAISettingsState> {

  private OpenAISettingsState state;

  public OpenAISettings() {
    this.state = new OpenAISettingsState();
  }

  public static OpenAISettings getInstance() {
    return ApplicationManager.getApplication()
        .getService(OpenAISettings.class);
  }

  @Override
  @NotNull
  public OpenAISettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
  }

  public void apply(OpenAISettingsState settingsState) {
    this.state = settingsState;
  }

}
