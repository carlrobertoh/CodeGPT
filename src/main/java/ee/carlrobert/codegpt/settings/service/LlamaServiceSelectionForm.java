package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;
import static java.util.stream.Collectors.toList;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;

public class LlamaServiceSelectionForm extends JPanel {

  private final LlamaServerPreferencesForm llamaServerPreferencesForm;
  private final LlamaRequestPreferencesForm llamaRequestPreferencesForm;

  public LlamaServiceSelectionForm() {
    llamaServerPreferencesForm = new LlamaServerPreferencesForm();
    llamaRequestPreferencesForm = new LlamaRequestPreferencesForm();
    init();
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServerPreferencesForm;
  }

  public LlamaRequestPreferencesForm getLlamaRequestPreferencesForm() {
    return llamaRequestPreferencesForm;
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
