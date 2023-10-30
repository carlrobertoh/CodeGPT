package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class LlamaServiceSelectionForm extends JPanel {

  private final PortField portField;
  private final LlamaModelPreferencesForm llamaModelPreferencesForm;

  public LlamaServiceSelectionForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    portField = new PortField(llamaSettings.getServerPort());

    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var serverProgressPanel = new ServerProgressPanel();
    llamaModelPreferencesForm = new LlamaModelPreferencesForm();

    var serverActionButton = new ServerActionButton(
        () -> {
          serverProgressPanel.startProgress("Starting a server...");
          // TODO: Move to LlamaModelPreferencesForm
          var modelPath = llamaModelPreferencesForm.isUseCustomLlamaModel() ?
              llamaModelPreferencesForm.getCustomLlamaModelPath() :
              "models/" + llamaModelPreferencesForm.getSelectedModel().getFileName();
          llamaServerAgent.startAgent(modelPath, serverProgressPanel);
        },
        () -> {
          serverProgressPanel.updateText("Stopping a server...");
          llamaServerAgent.stopAgent();
        },
        llamaServerAgent.isServerRunning());

    llamaModelPreferencesForm.addHuggingFaceModelChangeListener(huggingFaceModel ->
        serverActionButton.setEnabled(isModelExists(huggingFaceModel)));
    llamaModelPreferencesForm.addUseCustomModelChangeListener(serverActionButton::setEnabled);
    llamaModelPreferencesForm.addDownloadSuccessListener(() -> serverActionButton.setEnabled(true));

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(llamaModelPreferencesForm.getForm()))
        .addComponent(new TitledSeparator("Server Preferences"))
        .addComponent(withEmptyLeftBorder(FormBuilder.createFormBuilder()
            .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(serverActionButton))
            .getPanel()))
        .addVerticalGap(4)
        .addComponent(withEmptyLeftBorder(serverProgressPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  public void setServerPort(int serverPort) {
    portField.setNumber(serverPort);
  }

  public int getServerPort() {
    return portField.getNumber();
  }

  public LlamaModelPreferencesForm getLlamaModelPreferencesForm() {
    return llamaModelPreferencesForm;
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }
}
