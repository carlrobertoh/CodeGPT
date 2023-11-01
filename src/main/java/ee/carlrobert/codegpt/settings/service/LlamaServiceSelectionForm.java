package ee.carlrobert.codegpt.settings.service;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class LlamaServiceSelectionForm extends JPanel {

  private final LlamaModelPreferencesForm llamaModelPreferencesForm;
  private final PortField portField;
  private final IntegerField maxTokensField;

  public LlamaServiceSelectionForm() {
    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var serverRunning = llamaServerAgent.isServerRunning();
    portField = new PortField(LlamaSettingsState.getInstance().getServerPort());
    portField.setEnabled(!serverRunning);

    var serverProgressPanel = new ServerProgressPanel();
    llamaModelPreferencesForm = new LlamaModelPreferencesForm();

    maxTokensField = new IntegerField("max_tokens", 256, 4096);
    maxTokensField.setColumns(12);
    maxTokensField.setValue(2048);
    maxTokensField.setEnabled(!serverRunning);

    var serverButton = new JButton();
    serverButton.setText(serverRunning ? "Stop Server" : "Start Server");
    serverButton.setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
    serverButton.addActionListener(event -> {
      if (llamaModelPreferencesForm.isUseCustomLlamaModel()) {
        var customModelPath = llamaModelPreferencesForm.getCustomLlamaModelPath();
        if (customModelPath == null || customModelPath.isEmpty()) {
          OverlayUtils.showBalloon(
              "This is a required field",
              MessageType.ERROR,
              llamaModelPreferencesForm.getCustomModelPathBrowserButton());
          return;
        }
      } else {
        if (!isModelExists(llamaModelPreferencesForm.getSelectedModel())) {
          OverlayUtils.showBalloon(
              "Model is not downloaded",
              MessageType.ERROR,
              llamaModelPreferencesForm.getHuggingFaceModelComboBox());
          return;
        }
      }

      if (llamaServerAgent.isServerRunning()) {
        setFormEnabled(true);
        serverButton.setText("Start Server");
        serverButton.setIcon(Actions.Execute);
        serverProgressPanel.updateText("Stopping a server...");
        llamaServerAgent.stopAgent();
      } else {
        setFormEnabled(false);
        serverButton.setText("Stop Server");
        serverButton.setIcon(Actions.Suspend);
        serverProgressPanel.startProgress("Starting a server...");

        // TODO: Move to LlamaModelPreferencesForm
        var modelPath = llamaModelPreferencesForm.isUseCustomLlamaModel() ?
            llamaModelPreferencesForm.getCustomLlamaModelPath() :
            CodeGPTPlugin.getLlamaModelsPath() +
                File.separator +
                llamaModelPreferencesForm.getSelectedModel().getFileName();
        llamaServerAgent.startAgent(
            modelPath,
            maxTokensField.getValue(),
            portField.getNumber(),
            serverProgressPanel);
      }
    });

    var contextSizeHelpText = ComponentPanelBuilder.createCommentComponent("--ctx-size N", true);
    contextSizeHelpText.setBorder(JBUI.Borders.empty(0, 4));

    setFormEnabled(!llamaServerAgent.isServerRunning());
    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(llamaModelPreferencesForm.getForm()))
        .addComponent(new TitledSeparator("Server Preferences"))
        .addComponent(withEmptyLeftBorder(FormBuilder.createFormBuilder()
            .addLabeledComponent("Context size:", maxTokensField)
            .addComponentToRightColumn(contextSizeHelpText)
            .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(serverButton))
            .getPanel()))
        .addVerticalGap(4)
        .addComponent(withEmptyLeftBorder(serverProgressPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private void setFormEnabled(boolean enabled) {
    llamaModelPreferencesForm.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
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

  public int getContextSize() {
    return maxTokensField.getValue();
  }

  public void setContextSize(int contextSize) {
    maxTokensField.setValue(contextSize);
  }
}
