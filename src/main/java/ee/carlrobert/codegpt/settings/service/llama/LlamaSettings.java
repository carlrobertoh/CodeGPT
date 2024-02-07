package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.credentials.LlamaCredentialManager;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_LlamaSettings", storages = @Storage("CodeGPT_CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private LlamaSettingsState state = new LlamaSettingsState();

  @Override
  @NotNull
  public LlamaSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    this.state = state;
  }

  public static LlamaSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettings.class);
  }

  public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
    return !serviceSelectionForm.getCurrentLlamaFormState().equals(state)
        || !serviceSelectionForm.getLlamaServerPreferencesForm().getApiKey()
        .equals(LlamaCredentialManager.getInstance().getCredential());
  }
}
