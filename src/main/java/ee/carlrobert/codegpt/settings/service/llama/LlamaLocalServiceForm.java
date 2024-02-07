package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createComment;
import static ee.carlrobert.codegpt.ui.UIUtil.createTextFieldWithBrowseButton;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Panels;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.ServerStartupParams;
import ee.carlrobert.codegpt.settings.service.util.ServerProgressPanel;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Form containing fields for all {@link LlamaLocalSettings}.
 */
public class LlamaLocalServiceForm extends FormBuilder {

  private final JBRadioButton bundledServerRadioButton;
  private final JBRadioButton customServerRadioButton;
  private final TextFieldWithBrowseButton browsableCustomServerTextField;

  private final LlamaModelSelector modelSelector;
  private final PortField portField;
  private final IntegerField maxTokensField;
  private final IntegerField threadsField;
  private final JBTextField additionalParametersField;

  private final ServerAgent serverAgent;
  private final Consumer<Boolean> onServerAgentStateChanged;
  private final LlamaLocalSettings localSettings;

  public LlamaLocalServiceForm(LlamaLocalSettings settings, ServerAgent serverAgent,
      Consumer<Boolean> onServerAgentStateChanged) {
    this.localSettings = settings;
    this.serverAgent = serverAgent;
    this.onServerAgentStateChanged = onServerAgentStateChanged;

    bundledServerRadioButton = new JBRadioButton("Use bundled server",
        !settings.isUseCustomServer());
    customServerRadioButton = new JBRadioButton("Use custom server",
        settings.isUseCustomServer());
    browsableCustomServerTextField = createTextFieldWithBrowseButton(
        FileChooserDescriptorFactory.createSingleFileDescriptor());
    browsableCustomServerTextField.setEnabled(!settings.isServerRunning());
    browsableCustomServerTextField.setText(settings.getServerPath());

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

    additionalParametersField = new JBTextField(settings.getAdditionalCompileParameters(), 30);
    additionalParametersField.setEnabled(!serverRunning);

    modelSelector = new LlamaModelSelector(settings.getModel());
  }

  @Override
  public JPanel getPanel() {
    var serverProgressPanel = new ServerProgressPanel();
    serverProgressPanel.setBorder(JBUI.Borders.emptyRight(16));
    addComponent(new TitledSeparator("Server Executable"))
        .addComponent(UIUtil.createForm(bundledServerRadioButton, new JPanel(),
            customServerRadioButton, createCustomServerForm(), !localSettings.isUseCustomServer()))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.modelPreferences.title")))
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
        .addVerticalGap(4)
        .addComponentToRightColumn(
            createComment(
                "settingsConfigurable.service.llama.additionalParameters.comment"))
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.promptTemplate.comment"));
    return withEmptyLeftBorder(super.getPanel());
  }

  private JButton getServerButton(
      ServerAgent serverAgent,
      ServerProgressPanel serverProgressPanel) {
    var serverRunning = serverAgent.isServerRunning();
    var serverButton = new JButton();
    serverButton.setText(serverRunning
        ? CodeGPTBundle.get("settingsConfigurable.service.llama.stopServer.label")
        : CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
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
            new ServerStartupParams(getUsedServerPath(), modelSelector.getSelectedModel(),
                getLocalSettings()),
            serverProgressPanel,
            () -> {
              setFormEnabled(false);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverRunning"),
                  Actions.Checked,
                  SwingConstants.LEADING));
              onServerAgentStateChanged.accept(true);
            },
            () -> {
              setFormEnabled(true);
              serverButton.setText(
                  CodeGPTBundle.get("settingsConfigurable.service.llama.startServer.label"));
              serverButton.setIcon(Actions.Execute);
              serverProgressPanel.displayComponent(new JBLabel(
                  CodeGPTBundle.get(
                      "settingsConfigurable.service.llama.progress.serverTerminated"),
                  Actions.Cancel,
                  SwingConstants.LEADING));
              onServerAgentStateChanged.accept(false);
            });
      }
    });
    return serverButton;
  }

  private String getUsedServerPath() {
    return bundledServerRadioButton.isSelected() ? LlamaLocalSettings.BUNDLED_SERVER
        : browsableCustomServerTextField.getText();
  }

  private boolean validateModelConfiguration() {
    if (!modelSelector.isModelExists()) {
      if (modelSelector.getSelectedModel() instanceof HuggingFaceModel) {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get(
                "settingsConfigurable.service.llama.overlay.modelNotDownloaded.text"),
            MessageType.ERROR,
            modelSelector.getHuggingFaceModelComboBox());
      } else {
        OverlayUtil.showBalloon(
            CodeGPTBundle.get(
                "settingsConfigurable.service.llama.overlay.customModelNotExists.text"),
            MessageType.ERROR,
            modelSelector.getCustomModelPathField());
      }
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

  public void setFormEnabled(boolean enabled) {
    modelSelector.enableFields(enabled);
    portField.setEnabled(enabled);
    maxTokensField.setEnabled(enabled);
    threadsField.setEnabled(enabled);
    additionalParametersField.setEnabled(enabled);
  }

  public void setLocalSettings(LlamaLocalSettings settings) {
    LlamaCompletionModel model = settings.getModel();
    modelSelector.setSelectedModel(model);
    modelSelector.setInfillPromptTemplate(settings.getInfillPromptTemplate());
    modelSelector.setChatPromptTemplate(settings.getChatPromptTemplate());
    portField.setValue(settings.getServerPort());
    maxTokensField.setValue(settings.getContextSize());
    threadsField.setValue(settings.getThreads());
    additionalParametersField.setText(settings.getAdditionalCompileParameters());
    if (settings.isUseCustomServer()) {
      customServerRadioButton.setSelected(true);
      browsableCustomServerTextField.setText(settings.getServerPath());
    } else {
      bundledServerRadioButton.setSelected(true);
    }
  }

  public LlamaLocalSettings getLocalSettings() {
    return new LlamaLocalSettings(
        getUsedServerPath(),
        modelSelector.getSelectedModel(),
        modelSelector.getChatPromptTemplate(),
        modelSelector.getInfillPromptTemplate(),
        portField.getNumber(),
        maxTokensField.getValue(),
        threadsField.getValue(),
        additionalParametersField.getText()
    );
  }

  private JPanel createCustomServerForm() {
    var customModelHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.customServerPath.comment"),
        true);
    customModelHelpText.setBorder(JBUI.Borders.empty(0, 4));

    return FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.customServerPath.label"),
            browsableCustomServerTextField)
        .addComponentToRightColumn(customModelHelpText)
        .addVerticalGap(4)
        .getPanel();
  }

}
