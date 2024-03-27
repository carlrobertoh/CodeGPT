package ee.carlrobert.codegpt.settings.service.azure;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettings implements PersistentStateComponent<AzureSettingsState> {

  private AzureSettingsState state = new AzureSettingsState();

  @Override
  @NotNull
  public AzureSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    this.state = state;
  }

  public static AzureSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static AzureSettings getInstance() {
    return ApplicationManager.getApplication().getService(AzureSettings.class);
  }

  public boolean isModified(AzureSettingsForm form) {
    return !form.getCurrentState().equals(state)
        || !StringUtils.equals(
        form.getActiveDirectoryToken(),
        CredentialsStore.INSTANCE.getCredential(AZURE_ACTIVE_DIRECTORY_TOKEN))
        || !StringUtils.equals(
        form.getApiKey(),
        CredentialsStore.INSTANCE.getCredential(AZURE_OPENAI_API_KEY));
  }
}
