package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.createComment;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.LlamaHuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.llama.LlamaServerStartupParams;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.LocalSettings;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

public class LlamaLocalServerPreferencesForm {

  private final LlamaLocalModelPreferencesForm llamaLocalModelPreferencesForm;
  private final JBPasswordField apiKeyField;
  private final PortField portField;
  private final IntegerField maxTokensField;
  private final IntegerField threadsField;
  private final JBTextField additionalParametersField;

  public LlamaLocalServerPreferencesForm() {
    var localSettings = LlamaSettingsState.getInstance().getLocalSettings();

    var llamaServerAgent =
        ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var serverRunning = llamaServerAgent.isServerRunning();
    portField = new PortField(localSettings.getServerPort());
    portField.setEnabled(!serverRunning);

    maxTokensField = new IntegerField("max_tokens", 256, 4096);
    maxTokensField.setColumns(12);
    maxTokensField.setValue(localSettings.getContextSize());
    maxTokensField.setEnabled(!serverRunning);

    threadsField = new IntegerField("threads", 1, 256);
    threadsField.setColumns(12);
    threadsField.setValue(localSettings.getThreads());
    threadsField.setEnabled(!serverRunning);

    additionalParametersField = new JBTextField(localSettings.getAdditionalParameters(), 30);
    additionalParametersField.setEnabled(!serverRunning);

    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(LlamaCredentialsManager.getInstance().getApiKey());

    llamaLocalModelPreferencesForm = new LlamaLocalModelPreferencesForm(localSettings);
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    llamaLocalModelPreferencesForm.setSelectedModel(localSettings.getLlModel());
    llamaLocalModelPreferencesForm.setCustomLlamaModelPath(localSettings.getCustomModel());
    llamaLocalModelPreferencesForm.setUseCustomLlamaModel(localSettings.isUseCustomModel());
    llamaLocalModelPreferencesForm.setPromptTemplate(localSettings.getPromptTemplate());
    // TODO differentiate between local/remote apiKey?
    apiKeyField.setText(LlamaCredentialsManager.getInstance().getApiKey());
    portField.setValue(localSettings.getServerPort());
    maxTokensField.setValue(localSettings.getContextSize());
    threadsField.setValue(localSettings.getThreads());
    additionalParametersField.setText(localSettings.getAdditionalParameters());

  }


  public JComponent getForm(LlamaServerAgent llamaServerAgent) {
    var serverProgressPanel = new ServerProgressPanel();
    serverProgressPanel.setBorder(JBUI.Borders.emptyRight(16));
    return withEmptyLeftBorder(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.modelPreferences.title")))
        .addComponent(withEmptyLeftBorder(llamaLocalModelPreferencesForm.getForm()))
        .addVerticalGap(8)
        .addLabeledComponent(
            CodeGPTBundle.get("shared.port"),
            JBUI.Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(JBUI.Panels.simplePanel()
                    .addToCenter(serverProgressPanel)
                    .addToRight(getServerButton(llamaServerAgent, serverProgressPanel))))
        .addVerticalGap(4)
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
        .addVerticalGap(8)
        .getPanel());
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
        llamaServerAgent.startAgent(
            new LlamaServerStartupParams(
                llamaLocalModelPreferencesForm.getActualModelPath(),
                getLocalSettings()),
            serverProgressPanel,
            () -> {
              setFormEnabled(false);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverRunning"),
                  Actions.Checked,
                  SwingConstants.LEADING));
            },
            () -> {
              setFormEnabled(true);
              serverButton.setText(
                  CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
              serverButton.setIcon(Actions.Execute);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverTerminated"),
                  Actions.Cancel,
                  SwingConstants.LEADING));
            });
      }
    });
    return serverButton;
  }

  private boolean validateModelConfiguration() {
    return validateCustomModelPath() && validateSelectedModel();
  }


  private boolean validateCustomModelPath() {
    if (llamaLocalModelPreferencesForm.isUseCustomLlamaModel()) {
      var customModelPath = llamaLocalModelPreferencesForm.getCustomLlamaModelPath();
      if (customModelPath == null || customModelPath.isEmpty()) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get("validation.error.fieldRequired"),
            MessageType.ERROR,
            llamaLocalModelPreferencesForm.getBrowsableCustomModelTextField());
        return false;
      }
    }
    return true;
  }

  private boolean validateSelectedModel() {
    if (!llamaLocalModelPreferencesForm.isUseCustomLlamaModel()
        && !isModelExists(llamaLocalModelPreferencesForm.getSelectedModel())) {
      OverlayUtil.showBalloon(
          CodeGPTBundle.get("settingsConfigurable.service.llama.overlay.modelNotDownloaded.text"),
          MessageType.ERROR,
          llamaLocalModelPreferencesForm.getHuggingFaceModelComboBox());
      return false;
    }
    return true;
  }

  private boolean isModelExists(LlamaHuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getId());
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

  public void setFormEnabled(boolean enabled) {
    llamaLocalModelPreferencesForm.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
    threadsField.setEnabled(enabled);
    additionalParametersField.setEnabled(enabled);
  }

  public LlamaLocalSettings getLocalSettings() {
    return new LlamaLocalSettings(
        llamaLocalModelPreferencesForm.isUseCustomLlamaModel(),
        llamaLocalModelPreferencesForm.getCustomLlamaModelPath(),
        llamaLocalModelPreferencesForm.getSelectedModel(),
        llamaLocalModelPreferencesForm.getPromptTemplate(),
        portField.getNumber(),
        maxTokensField.getValue(),
        threadsField.getValue(),
        additionalParametersField.getText());
  }

}
