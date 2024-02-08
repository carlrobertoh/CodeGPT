package ee.carlrobert.codegpt.settings.service.ollama.form;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettingsState;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class OllamaSettingsForm extends JPanel {

  private final OllamaRequestPreferencesForm ollamaRequestPreferencesForm;

  public OllamaSettingsForm(OllamaSettingsState settings) {
    ollamaRequestPreferencesForm = new OllamaRequestPreferencesForm(settings);
    init();
  }

  public OllamaSettingsState getCurrentState() {
    var state = new OllamaSettingsState();
    state.setBaseHost(ollamaRequestPreferencesForm.getBaseHost());
    state.setTopK(ollamaRequestPreferencesForm.getTopK());
    state.setTopP(ollamaRequestPreferencesForm.getTopP());
    state.setRepeatPenalty(ollamaRequestPreferencesForm.getRepeatPenalty());

    var modelPreferencesForm = ollamaRequestPreferencesForm.getLlamaModelPreferencesForm();
    state.setOllamaModel(modelPreferencesForm.getSelectedModel());
    return state;
  }

  public void resetForm() {
    ollamaRequestPreferencesForm.resetForm(OllamaSettings.getCurrentState());
  }

  private void init() {
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Request Preferences"))
        .addComponent(withEmptyLeftBorder(ollamaRequestPreferencesForm.getForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  public void loadAvailableModels() {
    ollamaRequestPreferencesForm.getLlamaModelPreferencesForm().refreshAvailableModels();
  }
}
