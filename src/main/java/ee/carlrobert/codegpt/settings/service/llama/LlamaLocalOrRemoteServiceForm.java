package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createForm;

import com.intellij.ui.components.JBRadioButton;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import javax.swing.JPanel;

/**
 * Form containing {@link JBRadioButton} to toggle between({@link LlamaLocalServiceForm}) or
 * ({@link LlamaRemoteServiceForm}).
 */
public class LlamaLocalOrRemoteServiceForm {

  private final JBRadioButton runLocalServerRadioButton;
  private final JBRadioButton useExistingServerRadioButton;

  private final LlamaLocalServiceForm llamaLocalServiceForm;
  private final LlamaRemoteServiceForm llamaRemoteServiceForm;

  private final LlamaSettingsState llamaSettingsState;

  public LlamaLocalOrRemoteServiceForm(LlamaSettingsState llamaSettingsState,
      ServerAgent serverAgent) {
    this.llamaSettingsState = llamaSettingsState;
    runLocalServerRadioButton = new JBRadioButton("Run local server",
        llamaSettingsState.isRunLocalServer());
    useExistingServerRadioButton = new JBRadioButton("Use existing server",
        !llamaSettingsState.isRunLocalServer());

    llamaLocalServiceForm = new LlamaLocalServiceForm(llamaSettingsState.getLocalSettings(),
        serverAgent, this::onServerAgentStateChanged);
    llamaRemoteServiceForm = new LlamaRemoteServiceForm();
  }

  private void onServerAgentStateChanged(boolean isRunning) {
    setFormEnabled(isRunning);
    llamaSettingsState.getLocalSettings().setServerRunning(isRunning);
  }


  public JPanel getForm() {
    return createForm(runLocalServerRadioButton, llamaLocalServiceForm.getPanel(),
        useExistingServerRadioButton, llamaRemoteServiceForm.getPanel(),
        runLocalServerRadioButton.isSelected());
  }


  private void setFormEnabled(boolean enabled) {
    runLocalServerRadioButton.setEnabled(enabled);
    useExistingServerRadioButton.setEnabled(enabled);
    llamaLocalServiceForm.setFormEnabled(enabled);
  }

  public void setRunLocalServer(boolean runLocalServer) {
    runLocalServerRadioButton.setSelected(runLocalServer);
  }

  public boolean isRunLocalServer() {
    return runLocalServerRadioButton.isSelected();
  }

  public void setRemoteSettings(LlamaRemoteSettings remoteSettings) {
    llamaRemoteServiceForm.setRemoteSettings(remoteSettings);
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return llamaRemoteServiceForm.getRemoteSettings();
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    llamaLocalServiceForm.setLocalSettings(localSettings);
  }

  public LlamaLocalSettings getLocalSettings() {
    return llamaLocalServiceForm.getLocalSettings();
  }

  public String getLocalApiKey() {
    return llamaLocalServiceForm.getApiKey();
  }

  public String getRemoteApikey() {
    return llamaRemoteServiceForm.getApiKey();
  }
}
