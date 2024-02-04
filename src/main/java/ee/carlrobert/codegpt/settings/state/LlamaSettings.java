package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.manager.LlamaLocalCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaRemoteCredentialsManager;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private LlamaSettingsState state;

  public LlamaSettings() {
    this.state = new LlamaSettingsState();
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaSettings.class);
  }

  @Override
  @NotNull
  public LlamaSettingsState getState() {
    return this.state;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
    this.state.getLocalSettings()
        .setCredentials(LlamaLocalCredentialsManager.getInstance().getCredentials());
    this.state.getRemoteSettings()
        .setCredentials(LlamaRemoteCredentialsManager.getInstance().getCredentials());
  }

  public void apply(LlamaSettingsState settingsState) {
    this.state = settingsState;
    LlamaLocalCredentialsManager.getInstance()
        .apply(settingsState.getLocalSettings().getCredentials());
    LlamaRemoteCredentialsManager.getInstance()
        .apply(settingsState.getRemoteSettings().getCredentials());
  }
}
