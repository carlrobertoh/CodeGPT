package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaCppSettingsState extends LlamaSettingsState<LlamaRemoteSettings> implements
    PersistentStateComponent<LlamaCppSettingsState> {

  public LlamaCppSettingsState() {
    super(new LlamaRemoteSettings());
  }

  public LlamaCppSettingsState(boolean runLocalServer, LlamaLocalSettings localSettings,
      LlamaRemoteSettings remoteSettings, LlamaRequestSettings requestSettings) {
    super(runLocalServer, localSettings, remoteSettings, requestSettings);
  }

  @Override
  public LlamaCppSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull LlamaCppSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static LlamaCppSettingsState getInstance() {
    LlamaCppSettingsState service = ApplicationManager.getApplication()
        .getService(LlamaCppSettingsState.class);
    service.localSettings.setCredentialsManager(new LlamaCredentialsManager("LOCAL"));
    service.getRemoteSettings().setCredentialsManager(new LlamaCredentialsManager("REMOTE"));
    return service;
  }


}
