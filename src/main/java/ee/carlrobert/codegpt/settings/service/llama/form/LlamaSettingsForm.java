package ee.carlrobert.codegpt.settings.service.llama.form;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class LlamaSettingsForm extends JPanel {

  private final LlamaServerPreferencesForm llamaServerPreferencesForm;
  private final LlamaRequestPreferencesForm llamaRequestPreferencesForm;

  public LlamaSettingsForm(LlamaSettingsState settings) {
    llamaServerPreferencesForm = new LlamaServerPreferencesForm(settings);
    llamaRequestPreferencesForm = new LlamaRequestPreferencesForm(settings);
    init();
  }

  public LlamaSettingsState getCurrentFormState() {
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

    var modelPreferencesForm = llamaServerPreferencesForm.getLlamaModelPreferencesForm();
    state.setCustomLlamaModelPath(modelPreferencesForm.getCustomLlamaModelPath());
    state.setHuggingFaceModel(modelPreferencesForm.getSelectedModel());
    state.setUseCustomModel(modelPreferencesForm.isUseCustomLlamaModel());
    state.setLocalModelPromptTemplate(modelPreferencesForm.getPromptTemplate());
    state.setLocalModelInfillPromptTemplate(modelPreferencesForm.getInfillPromptTemplate());

    return state;
  }

  public void resetForm() {
    var state = LlamaSettings.getCurrentState();
    llamaServerPreferencesForm.resetForm(state);
    llamaRequestPreferencesForm.resetForm(state);
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServerPreferencesForm;
  }

  private void init() {
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.serverPreferences.title")))
        .addComponent(llamaServerPreferencesForm.getForm())
        .addComponent(new TitledSeparator("Request Preferences"))
        .addComponent(withEmptyLeftBorder(llamaRequestPreferencesForm.getForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }
}
