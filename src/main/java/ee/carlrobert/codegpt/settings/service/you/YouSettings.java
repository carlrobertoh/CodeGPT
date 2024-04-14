package ee.carlrobert.codegpt.settings.service.you;

import static ee.carlrobert.codegpt.credentials.Credential.getCredential;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.YOU_ACCOUNT_PASSWORD;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_YouSettings", storages = @Storage("CodeGPT_YouSettings.xml"))
public class YouSettings implements PersistentStateComponent<YouSettingsState> {

  private YouSettingsState state = new YouSettingsState();

  @Override
  @NotNull
  public YouSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull YouSettingsState state) {
    this.state = state;
  }

  public static YouSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static YouSettings getInstance() {
    return ApplicationManager.getApplication().getService(YouSettings.class);
  }

  public boolean isModified(YouSettingsForm form) {
    return !form.getCurrentState().equals(state)
            || !StringUtils.equals(form.getPassword(), getCredential(YOU_ACCOUNT_PASSWORD));
  }
}
