package ee.carlrobert.codegpt.settings.service.openai;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettings implements PersistentStateComponent<OpenAISettingsState> {

  private OpenAISettingsState state = new OpenAISettingsState();

  @Override
  @NotNull
  public OpenAISettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    this.state = state;
  }

  public static OpenAISettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static OpenAISettings getInstance() {
    return ApplicationManager.getApplication().getService(OpenAISettings.class);
  }

  public static boolean isCredentialSet() {
    return CredentialsStore.INSTANCE.isCredentialSet(OPENAI_API_KEY);
  }

  public boolean isModified(OpenAISettingsForm form) {
    return !form.getCurrentState().equals(state)
        || !StringUtils.equals(
        form.getApiKey(),
        CredentialsStore.INSTANCE.getCredential(OPENAI_API_KEY));
  }
}
