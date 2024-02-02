package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createApiKeyPanel;
import static ee.carlrobert.codegpt.ui.UIUtil.createComment;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Panels;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.llama.ServerStartupParams;
import ee.carlrobert.codegpt.completions.llama.CustomLamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.util.ServerProgressPanel;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.PromptTemplateField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Form containing fields for all {@link LlamaLocalSettings}
 */
public abstract class LlamaLocalServiceForm extends FormBuilder {

  private final LlamaModelSelector modelSelector;
  private final PortField portField;
  private final IntegerField maxTokensField;
  private final IntegerField threadsField;
  private final JBTextField additionalParametersField;
  private final JBPasswordField apiKeyField;

  private final PromptTemplateField promptTemplateField;

  private final LlamaCredentialsManager credentialsManager;

  private final ServerAgent serverAgent;

  public LlamaLocalServiceForm(LlamaLocalSettings settings, ServerAgent serverAgent) {
    this.credentialsManager = settings.getCredentialsManager();
    this.serverAgent = serverAgent;
    var serverRunning = serverAgent.isServerRunning();
    portField = new PortField(settings.getServerPort());
    portField.setEnabled(!serverRunning);

    maxTokensField = new IntegerField("max_tokens", 256, 4096);
    maxTokensField.setColumns(12);
    maxTokensField.setValue(settings.getContextSize());
    maxTokensField.setEnabled(!serverRunning);

    threadsField = new IntegerField("threads", 1, 256);
    threadsField.setColumns(12);
    threadsField.setValue(settings.getThreads());
    threadsField.setEnabled(!serverRunning);

    additionalParametersField = new JBTextField(settings.getAdditionalParameters(), 30);
    additionalParametersField.setEnabled(!serverRunning);

    apiKeyField = new JBPasswordField();

    modelSelector = new LlamaModelSelector(settings.getModel());

    promptTemplateField = new PromptTemplateField(
        LlamaSettingsState.getInstance().getRemoteSettings().getPromptTemplate(), true);
  }

  protected abstract boolean isModelExists(HuggingFaceModel model);

  public void setLocalSettings(LlamaLocalSettings settings) {
    LlamaCompletionModel model = settings.getModel();
    modelSelector.setSelectedModel(model);
    promptTemplateField.setPromptTemplate(settings.getPromptTemplate());
    portField.setValue(settings.getServerPort());
    maxTokensField.setValue(settings.getContextSize());
    threadsField.setValue(settings.getThreads());
    additionalParametersField.setText(settings.getAdditionalParameters());
    apiKeyField.setText(settings.getCredentialsManager().getApiKey());
  }

  @Override
  public JPanel getPanel() {
    var serverProgressPanel = new ServerProgressPanel();
    serverProgressPanel.setBorder(JBUI.Borders.emptyRight(16));
    addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.ollama.modelPreferences.title")))
        .addComponent(withEmptyLeftBorder(modelSelector.getComponent()))
        .addVerticalGap(8)
        .addLabeledComponent(
            CodeGPTBundle.get("shared.port"),
            Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(Panels.simplePanel()
                    .addToCenter(serverProgressPanel)
                    .addToRight(getServerButton(serverAgent, serverProgressPanel))))
        .addVerticalGap(4)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.ollama.contextSize.label"),
            maxTokensField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.contextSize.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.ollama.threads.label"),
            threadsField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.threads.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.ollama.additionalParameters.label"),
            additionalParametersField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.additionalParameters.comment"))
        .addComponent(UI.PanelFactory.panel(promptTemplateField)
            .withLabel(CodeGPTBundle.get("shared.promptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false).createPanel())
        .addVerticalGap(8)
        .addComponentFillVertically(new JPanel(), 0);
    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")));
    addComponent(
        createApiKeyPanel(credentialsManager.getApiKey(), apiKeyField).createPanel());
    return withEmptyLeftBorder(super.getPanel());
  }

  private JButton getServerButton(
      ServerAgent serverAgent,
      ServerProgressPanel serverProgressPanel) {
    var serverRunning = serverAgent.isServerRunning();
    var serverButton = new JButton();
    serverButton.setText(serverRunning
        ? CodeGPTBundle.get("settingsConfigurable.service.ollama.stopServer.label")
        : CodeGPTBundle.get("settingsConfigurable.service.ollama.startServer.label"));
    serverButton.setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
    serverButton.addActionListener(event -> {
      if (!validateModelConfiguration()) {
        return;
      }

      if (serverAgent.isServerRunning()) {
        enableForm(serverButton, serverProgressPanel);
        serverAgent.stopAgent();
      } else {
        disableForm(serverButton, serverProgressPanel);
        serverAgent.startAgent(
            new ServerStartupParams(modelSelector.getSelectedModel(), getLocalSettings()),
            serverProgressPanel,
            () -> {
              setFormEnabled(false);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get("settingsConfigurable.service.ollama.progress.serverRunning"),
                  Actions.Checked,
                  SwingConstants.LEADING));
            },
            () -> {
              setFormEnabled(true);
              serverButton.setText(
                  CodeGPTBundle.get("settingsConfigurable.service.ollama.startServer.label"));
              serverButton.setIcon(Actions.Execute);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get(
                      "settingsConfigurable.service.ollama.progress.serverTerminated"),
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
    LlamaCompletionModel model = modelSelector.getSelectedModel();
    if (model instanceof CustomLamaModel) {
      var customModelPath = ((CustomLamaModel) model).getModelPath();
      if (customModelPath == null || customModelPath.isEmpty()) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get("validation.error.fieldRequired"),
            MessageType.ERROR,
            modelSelector.getChooseCustomModelComponent().getComponent());
        return false;
      }
    }
    return true;
  }

  private boolean validateSelectedModel() {
    LlamaCompletionModel model = modelSelector.getSelectedModel();
    if (model instanceof HuggingFaceModel) {
      HuggingFaceModel model1 = (HuggingFaceModel) model;
      if (!isModelExists(model1)) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get(
                "settingsConfigurable.service.ollama.overlay.modelNotDownloaded.text"),
            MessageType.ERROR,
            modelSelector.getHuggingFaceModelComboBox());
        return false;
      }
    }
    return true;
  }



  private void enableForm(JButton serverButton, ServerProgressPanel progressPanel) {
    setFormEnabled(true);
    serverButton.setText(
        CodeGPTBundle.get("settingsConfigurable.service.ollama.startServer.label"));
    serverButton.setIcon(Actions.Execute);
    progressPanel.updateText(
        CodeGPTBundle.get("settingsConfigurable.service.ollama.progress.stoppingServer"));
  }

  private void disableForm(JButton serverButton, ServerProgressPanel progressPanel) {
    setFormEnabled(false);
    serverButton.setText(
        CodeGPTBundle.get("settingsConfigurable.service.ollama.stopServer.label"));
    serverButton.setIcon(Actions.Suspend);
    progressPanel.startProgress(
        CodeGPTBundle.get("settingsConfigurable.service.ollama.progress.startingServer"));
  }

  public void setFormEnabled(boolean enabled) {
    modelSelector.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
    threadsField.setEnabled(enabled);
    additionalParametersField.setEnabled(enabled);
    promptTemplateField.setEnabled(enabled);
  }

  public LlamaLocalSettings getLocalSettings() {
    LlamaLocalSettings localSettings = new LlamaLocalSettings(
        modelSelector.getSelectedModel(),
        promptTemplateField.getPromptTemplate(),
        portField.getNumber(),
        maxTokensField.getValue(),
        threadsField.getValue(),
        additionalParametersField.getText());
    localSettings.setCredentialsManager(credentialsManager);
    return localSettings;
  }

  public String getApiKey() {
    return apiKeyField != null ? new String(apiKeyField.getPassword()) : null;
  }

  public void setApiKey(String apiKey) {
    apiKeyField.setText(apiKey);
  }
}
