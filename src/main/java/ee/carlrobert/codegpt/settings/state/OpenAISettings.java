package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.manager.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettings implements PersistentStateComponent<OpenAISettingsState> {

  private final OpenAICredentialsManager credentialsManager;
  private OpenAISettingsState state;

  public OpenAISettings() {
    this.state = new OpenAISettingsState();
    this.credentialsManager = OpenAICredentialsManager.getInstance();
  }

  @NonInjectable
  public OpenAISettings(OpenAICredentialsManager credentialsManager) {
    this.state = new OpenAISettingsState();
    this.credentialsManager = credentialsManager;
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
    this.state.setCredentials(credentialsManager.getCredentials());
  }

  public void apply(OpenAISettingsState settingsState) {
    this.state = settingsState;
    credentialsManager.apply(settingsState.getCredentials());
  }

}
