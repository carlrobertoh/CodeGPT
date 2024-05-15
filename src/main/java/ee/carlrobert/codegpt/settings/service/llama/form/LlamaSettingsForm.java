package ee.carlrobert.codegpt.settings.service.llama.form;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.CodeCompletionConfigurationForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class LlamaSettingsForm extends JPanel {

  private final LlamaServerPreferencesForm llamaServerPreferencesForm;
  private final LlamaRequestPreferencesForm llamaRequestPreferencesForm;
  private final CodeCompletionConfigurationForm codeCompletionConfigurationForm;

  public LlamaSettingsForm(LlamaSettingsState settings) {
    llamaServerPreferencesForm = new LlamaServerPreferencesForm(settings);
    llamaRequestPreferencesForm = new LlamaRequestPreferencesForm(settings);
    codeCompletionConfigurationForm = new CodeCompletionConfigurationForm(
        settings.isCodeCompletionsEnabled(),
        null);
    init();
  }

  public LlamaSettingsState getCurrentState() {
    var state = new LlamaSettingsState();
    state.setTopK(llamaRequestPreferencesForm.getTopK());
    state.setTopP(llamaRequestPreferencesForm.getTopP());
    state.setMinP(llamaRequestPreferencesForm.getMinP());
    state.setRepeatPenalty(llamaRequestPreferencesForm.getRepeatPenalty());

    state.setRemoteModelPromptTemplate(llamaServerPreferencesForm.getPromptTemplate());
    state.setRemoteModelInfillPromptTemplate(llamaServerPreferencesForm.getInfillPromptTemplate());
    state.setRunLocalServer(llamaServerPreferencesForm.isRunLocalServer());
    state.setBaseHost(llamaServerPreferencesForm.getBaseHost());
    state.setServerPort(llamaServerPreferencesForm.getServerPort());
    state.setContextSize(llamaServerPreferencesForm.getContextSize());
    state.setThreads(llamaServerPreferencesForm.getThreads());
    state.setAdditionalParameters(llamaServerPreferencesForm.getAdditionalParameters());
    state.setAdditionalBuildParameters(llamaServerPreferencesForm.getAdditionalBuildParameters());
    state.setAdditionalEnvironmentVariables(
        llamaServerPreferencesForm.getAdditionalEnvironmentVariables());

    var modelPreferencesForm = llamaServerPreferencesForm.getLlamaModelPreferencesForm();
    state.setCustomLlamaModelPath(modelPreferencesForm.getCustomLlamaModelPath());
    state.setHuggingFaceModel(modelPreferencesForm.getSelectedModel());
    state.setUseCustomModel(modelPreferencesForm.isUseCustomLlamaModel());
    state.setLocalModelPromptTemplate(modelPreferencesForm.getPromptTemplate());
    state.setLocalModelInfillPromptTemplate(modelPreferencesForm.getInfillPromptTemplate());
    state.setCodeCompletionsEnabled(codeCompletionConfigurationForm.isCodeCompletionsEnabled());
    return state;
  }

  public void resetForm() {
    var state = LlamaSettings.getCurrentState();
    llamaServerPreferencesForm.resetForm(state);
    llamaRequestPreferencesForm.resetForm(state);
    codeCompletionConfigurationForm.setCodeCompletionsEnabled(state.isCodeCompletionsEnabled());
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServerPreferencesForm;
  }

  private void init() {
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Code Completions"))
        .addComponent(withEmptyLeftBorder(codeCompletionConfigurationForm.getForm()))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.serverPreferences.title")))
        .addComponent(llamaServerPreferencesForm.getForm())
        .addComponent(new TitledSeparator("Request Preferences"))
        .addComponent(withEmptyLeftBorder(llamaRequestPreferencesForm.getForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }
}
