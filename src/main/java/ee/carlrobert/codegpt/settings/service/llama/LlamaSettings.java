package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.LLAMA_API_KEY;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT", storages = @Storage("CodeGPT_LlamaSettings.xml"))
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
    // Catch if model's name has changed which could lead to
    // HuggingFaceModel or PromptTemplates not being found
    if (this.state.getHuggingFaceModel() == null) {
      this.state.setHuggingFaceModel(HuggingFaceModel.CODE_LLAMA_7B_Q4);
    }
    if (this.state.getRemoteModelInfillPromptTemplate() == null) {
      this.state.setRemoteModelInfillPromptTemplate(InfillPromptTemplate.LLAMA);
    }
    if (this.state.getLocalModelPromptTemplate() == null) {
      this.state.setLocalModelInfillPromptTemplate(InfillPromptTemplate.LLAMA);
    }
  }

  public static LlamaSettingsState getCurrentState() {
    return getInstance().getState();
  }

  /**
   * Code Completions enabled in settings and a model with InfillPromptTemplate selected.
   */
  public static boolean isCodeCompletionsPossible() {
    return getInstance().getState().isCodeCompletionsEnabled()
            && LlamaModel.findByHuggingFaceModel(getInstance().getState().getHuggingFaceModel())
                    .getInfillPromptTemplate() != null;
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettings.class);
  }

  public boolean isModified(LlamaSettingsForm form) {
    return !form.getCurrentState().equals(state)
        || !StringUtils.equals(
        form.getLlamaServerPreferencesForm().getApiKey(),
        CredentialsStore.INSTANCE.getCredential(LLAMA_API_KEY));
  }
}
