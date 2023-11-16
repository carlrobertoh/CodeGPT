package ee.carlrobert.codegpt.settings.service;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
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
import javax.swing.SwingConstants;

public class LlamaServiceSelectionForm extends JPanel {

  private final LlamaModelPreferencesForm llamaModelPreferencesForm;
  private final PortField portField;
  private final IntegerField maxTokensField;

  public LlamaServiceSelectionForm() {
    var llamaServerAgent =
        ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var serverRunning = llamaServerAgent.isServerRunning();
    portField = new PortField(LlamaSettingsState.getInstance().getServerPort());
    portField.setEnabled(!serverRunning);

    llamaModelPreferencesForm = new LlamaModelPreferencesForm();

    maxTokensField = new IntegerField("max_tokens", 256, 4096);
    maxTokensField.setColumns(12);
    maxTokensField.setValue(2048);
    maxTokensField.setEnabled(!serverRunning);

    var serverProgressPanel = new ServerProgressPanel();
    var serverButton = getServerButton(serverRunning, llamaServerAgent, serverProgressPanel);
    var contextSizeHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.contextSize.comment"),
        true);
    contextSizeHelpText.setBorder(JBUI.Borders.empty(0, 4));

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.modelPreferences.title")))
        .addComponent(withEmptyLeftBorder(llamaModelPreferencesForm.getForm()))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.serverPreferences.title")))
        .addComponent(withEmptyLeftBorder(FormBuilder.createFormBuilder()
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.llama.contextSize.label"),
                maxTokensField)
            .addComponentToRightColumn(contextSizeHelpText)
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.llama.port.label"),
                JBUI.Panels.simplePanel()
                    .addToLeft(portField)
                    .addToRight(serverButton))
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

  public int getContextSize() {
    return maxTokensField.getValue();
  }

  public void setContextSize(int contextSize) {
    maxTokensField.setValue(contextSize);
  }

  private JButton getServerButton(boolean serverRunning, LlamaServerAgent llamaServerAgent,
      ServerProgressPanel serverProgressPanel) {
    var serverButton = new JButton();
    serverButton.setText(serverRunning
        ? CodeGPTBundle.get("settingsConfigurable.service.llama.stopServer.label")
        : CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
    serverButton.setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
    serverButton.addActionListener(event -> {
      if (llamaModelPreferencesForm.isUseCustomLlamaModel()) {
        var customModelPath = llamaModelPreferencesForm.getCustomLlamaModelPath();
        if (customModelPath == null || customModelPath.isEmpty()) {
          OverlayUtils.showBalloon(
              CodeGPTBundle.get("validation.error.fieldRequired"),
              MessageType.ERROR,
              llamaModelPreferencesForm.getCustomModelPathBrowserButton());
          return;
        }
      } else {
        if (!isModelExists(llamaModelPreferencesForm.getSelectedModel())) {
          OverlayUtils.showBalloon(
              CodeGPTBundle.get(
                  "settingsConfigurable.service.llama.overlay.modelNotDownloaded.text"),
              MessageType.ERROR,
              llamaModelPreferencesForm.getHuggingFaceModelComboBox());
          return;
        }
      }

      if (llamaServerAgent.isServerRunning()) {
        setFormEnabled(true);
        serverButton.setText(
            CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
        serverButton.setIcon(Actions.Execute);
        serverProgressPanel.updateText(
            CodeGPTBundle.get("settingsConfigurable.service.llama.progress.stoppingServer"));
        llamaServerAgent.stopAgent();
      } else {
        setFormEnabled(false);
        serverButton.setText(
            CodeGPTBundle.get("settingsConfigurable.service.llama.stopServer.label"));
        serverButton.setIcon(Actions.Suspend);
        serverProgressPanel.startProgress(
            CodeGPTBundle.get("settingsConfigurable.service.llama.progress.startingServer"));

        // TODO: Move to LlamaModelPreferencesForm
        var modelPath = llamaModelPreferencesForm.isUseCustomLlamaModel()
            ? llamaModelPreferencesForm.getCustomLlamaModelPath()
            : CodeGPTPlugin.getLlamaModelsPath()
                + File.separator
                + llamaModelPreferencesForm.getSelectedModel().getFileName();
        llamaServerAgent.startAgent(
            modelPath,
            maxTokensField.getValue(),
            portField.getNumber(),
            serverProgressPanel,
            () -> {
              setFormEnabled(false);
              serverProgressPanel.displayComponent(
                  new JBLabel("Server running", Actions.Checked, SwingConstants.LEADING));
            });
      }
    });
    return serverButton;
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
}
