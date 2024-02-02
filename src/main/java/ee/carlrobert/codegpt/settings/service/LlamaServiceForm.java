package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.llama.LlamaLocalOrRemoteServiceForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaRequestsForm;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * Form containing all forms to configure using
 * {@link ee.carlrobert.codegpt.settings.service.ServiceType#LLAMA_CPP}
 */
public class LlamaServiceForm extends JPanel {

  private final LlamaLocalOrRemoteServiceForm llamaLocalOrRemoteServiceForm;
  private final LlamaRequestsForm llamaRequestsForm;

  public LlamaServiceForm(LlamaLocalOrRemoteServiceForm llamaLocalOrRemoteServiceForm,
      LlamaRequestSettings llamaRequestSettings) {
    this.llamaLocalOrRemoteServiceForm = llamaLocalOrRemoteServiceForm;
    llamaRequestsForm = new LlamaRequestsForm(llamaRequestSettings);
    init();
  }

  public LlamaLocalOrRemoteServiceForm getServerPreferencesForm() {
    return llamaLocalOrRemoteServiceForm;
  }

  public LlamaRequestsForm getRequestPreferencesForm() {
    return llamaRequestsForm;
  }

  private void init() {
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.serverPreferences.title")))
        .addComponent(llamaLocalOrRemoteServiceForm.getForm())
        .addComponent(new TitledSeparator("Request Preferences"))
        .addComponent(withEmptyLeftBorder(llamaRequestsForm.getForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

}
