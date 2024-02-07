package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
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

  public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
    var stateChanged = serviceSelectionForm.getCurrentOpenAIFormState() != state;
    return !serviceSelectionForm.getOpenAIApiKey()
        .equals(OpenAICredentialManager.getInstance().getCredential()) || stateChanged;
  }
}
