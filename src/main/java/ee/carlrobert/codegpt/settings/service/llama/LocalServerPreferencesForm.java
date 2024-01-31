package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.addApiKeyPanel;
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
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.ServerStartupParams;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.credentials.ServiceCredentialsManager;
import ee.carlrobert.codegpt.ui.ComponentWithStringValue;
import ee.carlrobert.codegpt.settings.service.ServerProgressPanel;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Form containing fields for all {@link LlamaLocalSettings}
 */
public abstract class LocalServerPreferencesForm {

  private final ModelSelectionForm localModelPreferencesForm;
  private final PortField portField;
  private final IntegerField maxTokensField;
  private final IntegerField threadsField;
  private final JBTextField additionalParametersField;
  private JBPasswordField apiKeyField;

  private final ServiceCredentialsManager credentialsManager;

  public LocalServerPreferencesForm(LlamaLocalSettings settings, ServerAgent serverAgent) {
    this.credentialsManager = settings.getCredentialsManager();
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

    if (credentialsManager.providesApiKey()) {
      apiKeyField = new JBPasswordField();
      apiKeyField.setColumns(30);
      apiKeyField.setText(credentialsManager.getApiKey());
    }

    localModelPreferencesForm = new ModelSelectionForm(settings) {
      @Override
      public ComponentWithStringValue getChooseCustomModelComponent() {
        JBTextField textField = new JBTextField(settings.getCustomModel(), 30);
        textField.setText(settings.getCustomModel());
        return new ComponentWithStringValue() {
          @Override
          public JComponent getComponent() {
            return textField;
          }

          @Override
          public String getValue() {
            return textField.getText();
          }

          @Override
          public void setValue(String value) {
            textField.setText(value);
          }
        };
      }
    };

  }

  protected abstract boolean isModelExists(HuggingFaceModel model);

  public void setLocalSettings(LlamaLocalSettings settings) {
    localModelPreferencesForm.setSelectedModel(settings.getLlModel());
    localModelPreferencesForm.setCustomModel(settings.getCustomModel());
    localModelPreferencesForm.setUseCustomModel(settings.isUseCustomModel());
    localModelPreferencesForm.setPromptTemplate(settings.getPromptTemplate());
    portField.setValue(settings.getServerPort());
    maxTokensField.setValue(settings.getContextSize());
    threadsField.setValue(settings.getThreads());
    additionalParametersField.setText(settings.getAdditionalParameters());
    ServiceCredentialsManager credentialsManager = settings.getCredentialsManager();
    if (credentialsManager.providesApiKey()) {
      apiKeyField.setText(credentialsManager.getApiKey());
    }
  }

  public JComponent getForm(ServerAgent serverAgent) {
    var serverProgressPanel = new ServerProgressPanel();
    serverProgressPanel.setBorder(JBUI.Borders.emptyRight(16));
    FormBuilder formBuilder = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.ollama.modelPreferences.title")))
        .addComponent(withEmptyLeftBorder(localModelPreferencesForm.getModelForm()))
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
        .addVerticalGap(8);
    if (credentialsManager.providesApiKey()) {
      addApiKeyPanel(credentialsManager.getApiKey(), formBuilder, apiKeyField);
    }
    return withEmptyLeftBorder(formBuilder.getPanel());
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
            new ServerStartupParams(
                localModelPreferencesForm.isUseCustomModel(),
                localModelPreferencesForm.getCustomModel(),
                localModelPreferencesForm.getSelectedModel(),
                getLocalSettings()),
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
    if (localModelPreferencesForm.isUseCustomModel()) {
      var customModelPath = localModelPreferencesForm.getCustomModel();
      if (customModelPath == null || customModelPath.isEmpty()) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get("validation.error.fieldRequired"),
            MessageType.ERROR,
            localModelPreferencesForm.getChooseCustomModelComponent().getComponent());
        return false;
      }
    }
    return true;
  }

  private boolean validateSelectedModel() {
    if (!localModelPreferencesForm.isUseCustomModel()
        && !isModelExists(localModelPreferencesForm.getSelectedModel())) {
      OverlayUtil.showBalloon(
          CodeGPTBundle.get("settingsConfigurable.service.ollama.overlay.modelNotDownloaded.text"),
          MessageType.ERROR,
          localModelPreferencesForm.getHuggingFaceModelComboBox());
      return false;
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
    localModelPreferencesForm.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
    threadsField.setEnabled(enabled);
    additionalParametersField.setEnabled(enabled);
  }

  public LlamaLocalSettings getLocalSettings() {
    LlamaLocalSettings localSettings = new LlamaLocalSettings(
        localModelPreferencesForm.isUseCustomModel(),
        localModelPreferencesForm.getCustomModel(),
        localModelPreferencesForm.getSelectedModel(),
        localModelPreferencesForm.getPromptTemplate(),
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
