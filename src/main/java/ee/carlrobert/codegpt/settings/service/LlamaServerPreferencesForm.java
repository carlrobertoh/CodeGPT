package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.createForm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBRadioButton;
import ee.carlrobert.codegpt.completions.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.RemoteSettings;
import ee.carlrobert.codegpt.ui.UIUtil.RadioButtonWithLayout;
import java.util.Map;
import javax.swing.JPanel;

public class LlamaServerPreferencesForm {

  private static final String RUN_LOCAL_SERVER_FORM_CARD_CODE = "RunLocalServerSettings";
  private static final String USE_EXISTING_SERVER_FORM_CARD_CODE = "UseExistingServerSettings";

  private final JBRadioButton runLocalServerRadioButton;
  private final JBRadioButton useExistingServerRadioButton;

  private final LlamaLocalServerPreferencesForm localServerPreferencesForm;
  private final LlamaRemoteServerPreferencesForm remoteServerPreferencesForm;


  public LlamaServerPreferencesForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    runLocalServerRadioButton = new JBRadioButton("Run local server",
        llamaSettings.isRunLocalServer());
    useExistingServerRadioButton = new JBRadioButton("Use existing server",
        !llamaSettings.isRunLocalServer());

    localServerPreferencesForm = new LlamaLocalServerPreferencesForm();
    remoteServerPreferencesForm = new LlamaRemoteServerPreferencesForm();

  }

  public JPanel getForm() {
    var llamaServerAgent =
        ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    return createForm(Map.of(
        RUN_LOCAL_SERVER_FORM_CARD_CODE, new RadioButtonWithLayout(runLocalServerRadioButton,
            localServerPreferencesForm.getForm(llamaServerAgent)),
        USE_EXISTING_SERVER_FORM_CARD_CODE, new RadioButtonWithLayout(useExistingServerRadioButton,
            remoteServerPreferencesForm.getForm())
    ), runLocalServerRadioButton.isSelected()
        ? RUN_LOCAL_SERVER_FORM_CARD_CODE
        : USE_EXISTING_SERVER_FORM_CARD_CODE);
  }


  private void setFormEnabled(boolean enabled) {
    // TODO: to be called when server is started/stopped
    runLocalServerRadioButton.setEnabled(enabled);
    useExistingServerRadioButton.setEnabled(enabled);
    localServerPreferencesForm.setFormEnabled(enabled);
    remoteServerPreferencesForm.setFormEnabled(enabled);
  }

  public void setRunLocalServer(boolean runLocalServer) {
    runLocalServerRadioButton.setSelected(runLocalServer);
  }

  public boolean isRunLocalServer() {
    return runLocalServerRadioButton.isSelected();
  }

  public void setRemoteSettings(RemoteSettings remoteSettings) {
    remoteServerPreferencesForm.setRemoteSettings(remoteSettings);
  }

  public RemoteSettings getRemoteSettings() {
    return remoteServerPreferencesForm.getRemoteSettings();
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    localServerPreferencesForm.setLocalSettings(localSettings);
  }

  public LlamaLocalSettings getLocalSettings() {
    return localServerPreferencesForm.getLocalSettings();
  }

  public String getUsedApiKey() {
    // TODO: differentiate between local/remote apiKey
    return LlamaCredentialsManager.getInstance().getApiKey();
  }
}
