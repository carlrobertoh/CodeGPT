package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createForm;

import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.state.llama.LocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.RemoteSettings;
import ee.carlrobert.codegpt.ui.UIUtil.RadioButtonWithLayout;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Form containing {@link JBRadioButton} to toggle between  running a server locally
 * ({@link LocalServerPreferencesForm}) or using an existing server
 * ({@link RemoteServerPreferencesForm})
 */
public abstract class ServerPreferencesForm {

  private static final String RUN_LOCAL_SERVER_FORM_CARD_CODE = "RunLocalServerSettings";
  private static final String USE_EXISTING_SERVER_FORM_CARD_CODE = "UseExistingServerSettings";

  private final JBRadioButton runLocalServerRadioButton;
  private final JBRadioButton useExistingServerRadioButton;

  private final LocalServerPreferencesForm localServerPreferencesForm;
  private final RemoteServerPreferencesForm remoteServerPreferencesForm;
  private final ServerAgent serverAgent;

  public ServerPreferencesForm(ServiceSettingsState serviceSettingsState, ServerAgent serverAgent,
      String servicePrefix) {
    this.serverAgent = serverAgent;
    runLocalServerRadioButton = new JBRadioButton("Run local server",
        serviceSettingsState.isRunLocalServer());
    useExistingServerRadioButton = new JBRadioButton("Use existing server",
        !serviceSettingsState.isRunLocalServer());

    localServerPreferencesForm = new LocalServerPreferencesForm(
        serviceSettingsState.getLocalSettings(), serverAgent) {
      @Override
      protected boolean isModelExists(HuggingFaceModel model) {
        return ServerPreferencesForm.this.isModelExists(model);
      }
    };
    remoteServerPreferencesForm = new RemoteServerPreferencesForm(
        serviceSettingsState.getRemoteSettings(), servicePrefix);
  }

  public abstract boolean isModelExists(HuggingFaceModel model);

  public void addAdditionalLocalFields(FormBuilder formBuilder) {
  }

  public void addAdditionalRemoteFields(FormBuilder formBuilder) {
  }

  public JPanel getForm() {
    return createForm(Map.of(
        RUN_LOCAL_SERVER_FORM_CARD_CODE, new RadioButtonWithLayout(runLocalServerRadioButton,
            localServerPreferencesForm.getForm(serverAgent)),
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

  public void setLocalSettings(LocalSettings localSettings) {
    localServerPreferencesForm.setLocalSettings(localSettings);
  }

  public LocalSettings getLocalSettings() {
    return localServerPreferencesForm.getLocalSettings();
  }

  public String getLocalApiKey() {
    return localServerPreferencesForm.getApiKey();
  }

  public String getRemoteApikey() {
    return remoteServerPreferencesForm.getApiKey();
  }
}
