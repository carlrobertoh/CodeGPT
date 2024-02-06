package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.manager.LlamaLocalCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaRemoteCredentialsManager;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private final LlamaLocalCredentialsManager localCredentialsManager;
  private final LlamaRemoteCredentialsManager remoteCredentialsManager;
  private LlamaSettingsState state;

  public LlamaSettings() {
    this.state = new LlamaSettingsState();
    this.localCredentialsManager = LlamaLocalCredentialsManager.getInstance();
    this.remoteCredentialsManager = LlamaRemoteCredentialsManager.getInstance();
  }

  @NonInjectable
  public LlamaSettings(LlamaLocalCredentialsManager localCredentialsManager,
      LlamaRemoteCredentialsManager remoteCredentialsManager) {
    this.state = new LlamaSettingsState();
    this.localCredentialsManager = localCredentialsManager;
    this.remoteCredentialsManager = remoteCredentialsManager;
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
        .setCredentials(localCredentialsManager.getCredentials());
    this.state.getRemoteSettings()
        .setCredentials(remoteCredentialsManager.getCredentials());
  }

  public void apply(LlamaSettingsState settingsState) {
    this.state = settingsState;
    localCredentialsManager.apply(settingsState.getLocalSettings().getCredentials());
    remoteCredentialsManager.apply(settingsState.getRemoteSettings().getCredentials());
  }
}
