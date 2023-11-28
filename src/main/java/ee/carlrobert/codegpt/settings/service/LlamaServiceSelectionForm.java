package ee.carlrobert.codegpt.settings.service;

import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.util.OverlayUtil;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LlamaServiceSelectionForm extends JPanel {

  private final LlamaModelPreferencesForm llamaModelPreferencesForm;
  private final PortField portField;
  private final IntegerField maxTokensField;
  private final IntegerField threadsField;
  private final JBTextField additionalParametersField;

  public LlamaServiceSelectionForm() {
    var llamaServerAgent =
        ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var serverRunning = llamaServerAgent.isServerRunning();
    portField = new PortField(LlamaSettingsState.getInstance().getServerPort());
    portField.setEnabled(!serverRunning);

    llamaModelPreferencesForm = new LlamaModelPreferencesForm();

    var llamaSettings = LlamaSettingsState.getInstance();
    maxTokensField = new IntegerField("max_tokens", 256, 4096);
    maxTokensField.setColumns(12);
    maxTokensField.setValue(llamaSettings.getContextSize());
    maxTokensField.setEnabled(!serverRunning);

    threadsField = new IntegerField("threads", 1, 256);
    threadsField.setColumns(12);
    threadsField.setValue(llamaSettings.getThreads());
    threadsField.setEnabled(!serverRunning);

    additionalParametersField = new JBTextField(llamaSettings.getAdditionalParameters(), 30);
    additionalParametersField.setEnabled(!serverRunning);

    init(llamaServerAgent);
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

  public void setThreads(int threads) {
    threadsField.setValue(threads);
  }

  public int getThreads() {
    return threadsField.getValue();
  }

  public void setAdditionalParameters(String additionalParameters) {
    additionalParametersField.setText(additionalParameters);
  }

  public String getAdditionalParameters() {
    return additionalParametersField.getText();
  }

  public List<String> getListOfAdditionalParameters() {
    if (additionalParametersField.getText().trim().isEmpty()) {
      return Collections.emptyList();
    }
    var parameters = additionalParametersField.getText().split(",");
    return Arrays.stream(parameters)
        .map(String::trim)
        .collect(toList());
  }

  private void init(LlamaServerAgent llamaServerAgent) {
    var serverProgressPanel = new ServerProgressPanel();
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
            .addComponentToRightColumn(
                createComment("settingsConfigurable.service.llama.contextSize.comment"))
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.llama.threads.label"),
                threadsField)
            .addComponentToRightColumn(
                createComment("settingsConfigurable.service.llama.threads.comment"))
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.llama.additionalParameters.label"),
                additionalParametersField)
            .addComponentToRightColumn(
                createComment("settingsConfigurable.service.llama.additionalParameters.comment"))
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.llama.port.label"),
                JBUI.Panels.simplePanel()
                    .addToLeft(portField)
                    .addToRight(getServerButton(llamaServerAgent, serverProgressPanel)))
            .getPanel()))
        .addVerticalGap(4)
        .addComponent(withEmptyLeftBorder(serverProgressPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  private JLabel createComment(String messageKey) {
    var comment = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get(messageKey), true);
    comment.setBorder(JBUI.Borders.empty(0, 4));
    return comment;
  }

  private JButton getServerButton(
      LlamaServerAgent llamaServerAgent,
      ServerProgressPanel serverProgressPanel) {
    var serverRunning = llamaServerAgent.isServerRunning();
    var serverButton = new JButton();
    serverButton.setText(serverRunning
        ? CodeGPTBundle.get("settingsConfigurable.service.llama.stopServer.label")
        : CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
    serverButton.setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
    serverButton.addActionListener(event -> {
      if (!validateModelConfiguration()) {
        return;
      }

      if (llamaServerAgent.isServerRunning()) {
        enableForm(serverButton, serverProgressPanel);
        llamaServerAgent.stopAgent();
      } else {
        disableForm(serverButton, serverProgressPanel);
        llamaServerAgent.startAgent(this, serverProgressPanel, () -> {
          setFormEnabled(false);
          serverProgressPanel.displayComponent(
              new JBLabel("Server running", Actions.Checked, SwingConstants.LEADING));
        });
      }
    });
    return serverButton;
  }

  private boolean validateModelConfiguration() {
    return validateCustomModelPath() && validateSelectedModel();
  }

  private boolean validateCustomModelPath() {
    if (llamaModelPreferencesForm.isUseCustomLlamaModel()) {
      var customModelPath = llamaModelPreferencesForm.getCustomLlamaModelPath();
      if (customModelPath == null || customModelPath.isEmpty()) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get("validation.error.fieldRequired"),
            MessageType.ERROR,
            llamaModelPreferencesForm.getCustomModelPathBrowserButton());
        return false;
      }
    }
    return true;
  }

  private boolean validateSelectedModel() {
    if (!llamaModelPreferencesForm.isUseCustomLlamaModel() && !isModelExists(
        llamaModelPreferencesForm.getSelectedModel())) {
      OverlayUtil.showBalloon(
          CodeGPTBundle.get("settingsConfigurable.service.llama.overlay.modelNotDownloaded.text"),
          MessageType.ERROR,
          llamaModelPreferencesForm.getHuggingFaceModelComboBox());
      return false;
    }
    return true;
  }

  private void enableForm(JButton serverButton, ServerProgressPanel progressPanel) {
    setFormEnabled(true);
    serverButton.setText(
        CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
    serverButton.setIcon(Actions.Execute);
    progressPanel.updateText(
        CodeGPTBundle.get("settingsConfigurable.service.llama.progress.stoppingServer"));
  }

  private void disableForm(JButton serverButton, ServerProgressPanel progressPanel) {
    setFormEnabled(false);
    serverButton.setText(
        CodeGPTBundle.get("settingsConfigurable.service.llama.stopServer.label"));
    serverButton.setIcon(Actions.Suspend);
    progressPanel.startProgress(
        CodeGPTBundle.get("settingsConfigurable.service.llama.progress.startingServer"));
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private void setFormEnabled(boolean enabled) {
    llamaModelPreferencesForm.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
    threadsField.setEnabled(enabled);
    additionalParametersField.setEnabled(enabled);
  }
}
