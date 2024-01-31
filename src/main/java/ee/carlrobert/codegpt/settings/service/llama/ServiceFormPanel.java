package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRequestSettings;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class ServiceFormPanel extends JPanel {

  private final ServerPreferencesForm serverPreferencesForm;
  private final RequestPreferencesForm requestPreferencesForm;

  public ServiceFormPanel(ServerPreferencesForm serverPreferencesForm,
      LlamaRequestSettings llamaRequestSettings) {
    this.serverPreferencesForm = serverPreferencesForm;
    requestPreferencesForm = new RequestPreferencesForm(llamaRequestSettings);
    init();
  }

  public ServerPreferencesForm getServerPreferencesForm() {
    return serverPreferencesForm;
  }

  public RequestPreferencesForm getRequestPreferencesForm() {
    return requestPreferencesForm;
  }

  private void init() {
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.serverPreferences.title")))
        .addComponent(serverPreferencesForm.getForm())
        .addComponent(new TitledSeparator("Request Preferences"))
        .addComponent(withEmptyLeftBorder(requestPreferencesForm.getForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

}
