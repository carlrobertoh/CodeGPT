package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class LlamaServiceSelectionForm extends JPanel {

  private final LlamaServerPreferencesForm llamaServerPreferencesForm;
  private final LlamaRequestPreferencesForm llamaRequestPreferencesForm;

  public LlamaServiceSelectionForm() {
    llamaServerPreferencesForm = new LlamaServerPreferencesForm();
    llamaRequestPreferencesForm = new LlamaRequestPreferencesForm();
    init();
  }

  public void setRunLocalServer(boolean runLocalServer) {
    llamaServerPreferencesForm.setRunLocalServer(runLocalServer);
  }

  public boolean isRunLocalServer() {
    return llamaServerPreferencesForm.isRunLocalServer();
  }

  public void setBaseHost(String baseHost) {
    llamaServerPreferencesForm.setBaseHost(baseHost);
  }

  public String getBaseHost() {
    return llamaServerPreferencesForm.getBaseHost();
  }

  public void setServerPort(int serverPort) {
    llamaServerPreferencesForm.setServerPort(serverPort);
  }

  public int getServerPort() {
    return llamaServerPreferencesForm.getServerPort();
  }

  public LlamaServerPreferencesForm getLlamaServerPreferencesForm() {
    return llamaServerPreferencesForm;
  }

  public LlamaModelPreferencesForm getLlamaModelPreferencesForm() {
    return llamaServerPreferencesForm.getLlamaModelPreferencesForm();
  }

  public LlamaRequestPreferencesForm getLlamaRequestPreferencesForm() {
    return llamaRequestPreferencesForm;
  }

  public int getContextSize() {
    return llamaServerPreferencesForm.getContextSize();
  }

  public void setContextSize(int contextSize) {
    llamaServerPreferencesForm.setContextSize(contextSize);
  }

  public void setThreads(int threads) {
    llamaServerPreferencesForm.setThreads(threads);
  }

  public int getThreads() {
    return llamaServerPreferencesForm.getThreads();
  }

  public void setAdditionalParameters(String additionalParameters) {
    llamaServerPreferencesForm.setAdditionalParameters(additionalParameters);
  }

  public String getAdditionalParameters() {
    return llamaServerPreferencesForm.getAdditionalParameters();
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
