package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private LlamaSettingsState state;

  public LlamaSettings() {
    this.state = new LlamaSettingsState();
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettings.class);
  }

  @Override
  @NotNull
  public LlamaSettingsState getState() {
    return this.state;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
  }

  public void apply(LlamaSettingsState settingsState) {
    this.state = settingsState;
  }
}
