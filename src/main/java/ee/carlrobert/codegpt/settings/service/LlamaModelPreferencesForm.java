package ee.carlrobert.codegpt.settings.service;

import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Map;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class LlamaModelPreferencesForm {

  private static final Map<Integer, Map<Integer, ModelDetails>> modelDetailsMap = Map.of(
      7, Map.of(
          3, new ModelDetails(3.30, 5.80),
          4, new ModelDetails(4.08, 6.58),
          5, new ModelDetails(4.78, 7.28)),
      13, Map.of(
          3, new ModelDetails(6.34, 8.84),
          4, new ModelDetails(7.87, 10.37),
          5, new ModelDetails(9.23, 11.73)),
      34, Map.of(
          3, new ModelDetails(16.28, 18.78),
          4, new ModelDetails(20.22, 22.72),
          5, new ModelDetails(23.84, 26.34)));

  private final TextFieldWithBrowseButton customModelPathBrowserButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final ComboBox<PromptTemplate> promptTemplateComboBox;
  private final JBLabel modelExistsIcon;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;
  private final JBCheckBox useCustomModelCheckBox;
  private final JBLabel helpIcon;
  private final JPanel downloadModelActionLinkWrapper;
  private final JBLabel progressLabel;
  private final JBLabel modelDetailsLabel;

  public TextFieldWithBrowseButton getCustomModelPathBrowserButton() {
    return customModelPathBrowserButton;
  }

  public ComboBox<HuggingFaceModel> getHuggingFaceModelComboBox() {
    return huggingFaceModelComboBox;
  }

  public LlamaModelPreferencesForm() {
    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    var llamaSettings = LlamaSettingsState.getInstance();
    customModelPathBrowserButton = createCustomModelPathBrowseButton(
        llamaSettings.isUseCustomModel() && !llamaServerAgent.isServerRunning());
    customModelPathBrowserButton.setText(llamaSettings.getCustomLlamaModelPath());
    progressLabel = new JBLabel("");
    progressLabel.setBorder(JBUI.Borders.emptyLeft(2));
    progressLabel.setFont(JBUI.Fonts.smallFont());
    modelExistsIcon = new JBLabel(Actions.Checked);
    modelExistsIcon.setVisible(isModelExists(llamaSettings.getHuggingFaceModel()));
    helpIcon = new JBLabel(General.ContextHelp);
    huggingFaceComboBoxModel = new DefaultComboBoxModel<>();
    var llm = llamaSettings.getHuggingFaceModel();
    var llamaModel = LlamaModel.findByHuggingFaceModel(llm);

    var selectableModels = llamaModel.getHuggingFaceModels().stream()
        .filter(model -> model.getParameterSize() == llm.getParameterSize())
        .collect(toList());
    huggingFaceComboBoxModel.addAll(selectableModels);
    huggingFaceComboBoxModel.setSelectedItem(selectableModels.get(0));
    downloadModelActionLinkWrapper = new JPanel(new BorderLayout());
    downloadModelActionLinkWrapper.setBorder(JBUI.Borders.emptyLeft(2));
    downloadModelActionLinkWrapper.add(
        createDownloadModelLink(
            progressLabel,
            downloadModelActionLinkWrapper,
            huggingFaceComboBoxModel),
        BorderLayout.WEST);
    modelDetailsLabel = new JBLabel();
    huggingFaceModelComboBox = createHuggingFaceComboBox(
        huggingFaceComboBoxModel,
        modelExistsIcon,
        modelDetailsLabel,
        downloadModelActionLinkWrapper);
    huggingFaceModelComboBox.setEnabled(!llamaServerAgent.isServerRunning());
    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    var initialModelSizes = llamaModel.getSortedUniqueModelSizes().stream()
        .map(ModelSize::new)
        .collect(toList());
    modelSizeComboBoxModel.addAll(initialModelSizes);
    modelSizeComboBoxModel.setSelectedItem(initialModelSizes.get(0));
    var modelComboBoxModel = new EnumComboBoxModel<>(LlamaModel.class);
    modelComboBox = createModelComboBox(modelComboBoxModel, llamaModel, modelSizeComboBoxModel);
    modelComboBox.setEnabled(!llamaServerAgent.isServerRunning());
    modelSizeComboBox = createModelSizeComboBox(
        modelComboBoxModel,
        modelSizeComboBoxModel,
        huggingFaceComboBoxModel);
    modelSizeComboBox.setEnabled(
        initialModelSizes.size() > 1 && !llamaServerAgent.isServerRunning());
    promptTemplateComboBox = new ComboBox<>(new EnumComboBoxModel<>(PromptTemplate.class));
    promptTemplateComboBox.setSelectedItem(llamaSettings.getPromptTemplate());
    promptTemplateComboBox.setEnabled(
        llamaSettings.isUseCustomModel() && !llamaServerAgent.isServerRunning());
    promptTemplateComboBox.setPreferredSize(modelComboBox.getPreferredSize());
    useCustomModelCheckBox = new JBCheckBox(CodeGPTBundle.get(
        "settingsConfigurable.service.llama.useCustomModel.label"),
        llamaSettings.isUseCustomModel());
    useCustomModelCheckBox.setEnabled(!llamaServerAgent.isServerRunning());
    useCustomModelCheckBox.addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      customModelPathBrowserButton.setEnabled(selected && !llamaServerAgent.isServerRunning());
      promptTemplateComboBox.setEnabled(selected && !llamaServerAgent.isServerRunning());
      modelComboBox.setEnabled(!selected);
      modelSizeComboBox.setEnabled((!selected));
      huggingFaceModelComboBox.setEnabled((!selected));
    });
  }

  public JPanel getForm() {
    var customModelHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.comment"),
        true);
    customModelHelpText.setBorder(JBUI.Borders.empty(0, 4));
    var quantizationHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.quantization.comment"),
        true);
    quantizationHelpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(8));
    modelComboBoxWrapper.add(helpIcon);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    var huggingFaceModelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    huggingFaceModelComboBoxWrapper.add(huggingFaceModelComboBox);
    huggingFaceModelComboBoxWrapper.add(Box.createHorizontalStrut(8));
    huggingFaceModelComboBoxWrapper.add(modelDetailsLabel);

    return FormBuilder.createFormBuilder()
        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.shared.model.label"),
            modelComboBoxWrapper)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.modelSize.label"),
            modelSizeComboBox)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.quantization.label"),
            huggingFaceModelComboBoxWrapper)
        .addComponentToRightColumn(quantizationHelpText)
        .addComponentToRightColumn(downloadModelActionLinkWrapper)
        .addComponentToRightColumn(progressLabel)
        .addVerticalGap(8)
        .addComponent(useCustomModelCheckBox)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.label"),
            promptTemplateComboBox)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.label"),
            customModelPathBrowserButton)
        .addComponentToRightColumn(customModelHelpText)
        .addVerticalGap(4)
        .getPanel();
  }

  public void enableFields(boolean enabled) {
    modelComboBox.setEnabled(enabled);
    modelSizeComboBox.setEnabled(enabled);
    huggingFaceModelComboBox.setEnabled(enabled);
    useCustomModelCheckBox.setEnabled(enabled);
    promptTemplateComboBox.setEnabled(enabled && useCustomModelCheckBox.isSelected());
    customModelPathBrowserButton.setEnabled(enabled && useCustomModelCheckBox.isSelected());
  }

  private static class ModelDetails {

    double fileSize;
    double maxRAMRequired;

    public ModelDetails(double fileSize, double maxRAMRequired) {
      this.fileSize = fileSize;
      this.maxRAMRequired = maxRAMRequired;
    }
  }

  private String getHuggingFaceModelDetailsHtml(HuggingFaceModel model) {
    int parameterSize = model.getParameterSize();
    int quantization = model.getQuantization();

    if (!modelDetailsMap.containsKey(parameterSize)) {
      return "";
    }

    ModelDetails details = modelDetailsMap.get(parameterSize).get(quantization);
    if (details == null) {
      return "";
    }

    return String.format("<html>"
        + "<p style=\"margin: 0\"><small>File Size: <strong>%.2f GB</strong></small></p>"
        + "<p style=\"margin: 0\"><small>Max RAM Required: <strong>%.2f GB</strong></small></p>"
        + "</html>", details.fileSize, details.maxRAMRequired);
  }

  public void setSelectedModel(HuggingFaceModel model) {
    huggingFaceComboBoxModel.setSelectedItem(model);
  }

  public HuggingFaceModel getSelectedModel() {
    return (HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem();
  }

  public void setCustomLlamaModelPath(String modelPath) {
    customModelPathBrowserButton.setText(modelPath);
  }

  public String getCustomLlamaModelPath() {
    return customModelPathBrowserButton.getText();
  }

  public void setUseCustomLlamaModel(boolean useCustomLlamaModel) {
    useCustomModelCheckBox.setSelected(useCustomLlamaModel);
  }

  public boolean isUseCustomLlamaModel() {
    return useCustomModelCheckBox.isSelected();
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    promptTemplateComboBox.setSelectedItem(promptTemplate);
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplateComboBox.getItem();
  }

  private ComboBox<LlamaModel> createModelComboBox(
      EnumComboBoxModel<LlamaModel> llamaModelEnumComboBoxModel,
      LlamaModel llamaModel,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel) {
    var comboBox = new ComboBox<>(llamaModelEnumComboBoxModel);
    comboBox.setPreferredSize(new Dimension(280, comboBox.getPreferredSize().height));
    comboBox.setSelectedItem(llamaModel);
    comboBox.addItemListener(e -> {
      var selectedModel = (LlamaModel) e.getItem();
      var modelSizes = selectedModel.getSortedUniqueModelSizes().stream()
          .map(ModelSize::new)
          .collect(toList());

      modelSizeComboBoxModel.removeAllElements();
      modelSizeComboBoxModel.addAll(modelSizes);
      modelSizeComboBoxModel.setSelectedItem(modelSizes.get(0));
      modelSizeComboBox.setEnabled(modelSizes.size() > 1);

      var huggingFaceModels = selectedModel.getHuggingFaceModels().stream()
          .filter(model -> {
            var size = ((ModelSize) modelSizeComboBoxModel.getSelectedItem()).getSize();
            return size == model.getParameterSize();
          })
          .collect(toList());

      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(huggingFaceModels);
      huggingFaceComboBoxModel.setSelectedItem(huggingFaceModels.get(0));
    });
    return comboBox;
  }

  private ComboBox<ModelSize> createModelSizeComboBox(
      EnumComboBoxModel<LlamaModel> llamaModelComboBoxModel,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel,
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel) {
    var comboBox = new ComboBox<>(modelSizeComboBoxModel);
    comboBox.setPreferredSize(modelComboBox.getPreferredSize());
    comboBox.setSelectedItem(modelSizeComboBoxModel.getSelectedItem());
    comboBox.addItemListener(e -> {
      var selectedModel = llamaModelComboBoxModel.getSelectedItem();
      var models = selectedModel.getHuggingFaceModels().stream()
          .filter(model -> {
            var selectedModelSize = (ModelSize) modelSizeComboBoxModel.getSelectedItem();
            return selectedModelSize != null
                && selectedModelSize.getSize() == model.getParameterSize();
          })
          .collect(toList());
      if (!models.isEmpty()) {
        huggingFaceComboBoxModel.removeAllElements();
        huggingFaceComboBoxModel.addAll(models);
        huggingFaceComboBoxModel.setSelectedItem(models.get(0));
      }
    });
    return comboBox;
  }

  private ComboBox<HuggingFaceModel> createHuggingFaceComboBox(
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel,
      JBLabel modelExistsIcon,
      JBLabel modelDetailsLabel,
      JPanel downloadModelActionLinkWrapper) {
    var comboBox = new ComboBox<>(huggingFaceComboBoxModel);
    comboBox.addItemListener(e -> {
      var selectedModel = (HuggingFaceModel) e.getItem();
      var modelExists = isModelExists(selectedModel);

      updateModelHelpTooltip(selectedModel);
      modelDetailsLabel.setText(getHuggingFaceModelDetailsHtml(selectedModel));
      modelExistsIcon.setVisible(modelExists);
      downloadModelActionLinkWrapper.setVisible(!modelExists);
    });
    return comboBox;
  }

  private TextFieldWithBrowseButton createCustomModelPathBrowseButton(boolean enabled) {
    var browseButton = new TextFieldWithBrowseButton();
    browseButton.setEnabled(enabled);

    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    fileChooserDescriptor.setHideIgnored(false);
    browseButton.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor));
    return browseButton;
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private AnActionLink createCancelDownloadLink(
      JBLabel progressLabel,
      JPanel actionLinkWrapper,
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel,
      ProgressIndicator progressIndicator) {
    return new AnActionLink(
        CodeGPTBundle.get("settingsConfigurable.service.llama.cancelDownloadLink.label"),
        new AnAction() {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            SwingUtilities.invokeLater(() -> {
              configureFieldsForDownloading(false);
              updateActionLink(
                  actionLinkWrapper,
                  createDownloadModelLink(
                      progressLabel,
                      actionLinkWrapper,
                      huggingFaceComboBoxModel));
              progressIndicator.cancel();
            });
          }
        });
  }

  private void updateActionLink(JPanel actionLinkWrapper, AnActionLink actionLink) {
    actionLinkWrapper.removeAll();
    actionLinkWrapper.add(actionLink, BorderLayout.WEST);
    actionLinkWrapper.revalidate();
    actionLinkWrapper.repaint();
  }

  void configureFieldsForDownloading(boolean downloading) {
    progressLabel.setText("");
    progressLabel.setVisible(downloading);
    modelComboBox.setEnabled(!downloading);
    modelSizeComboBox.setEnabled(!downloading);
    huggingFaceModelComboBox.setEnabled(!downloading);
    modelExistsIcon.setVisible(!downloading);
  }

  private AnActionLink createDownloadModelLink(
      JBLabel progressLabel,
      JPanel actionLinkWrapper,
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel) {
    return new AnActionLink(
        CodeGPTBundle.get("settingsConfigurable.service.llama.downloadModelLink.label"),
        new DownloadModelAction(
            progressIndicator -> {
              SwingUtilities.invokeLater(() -> {
                configureFieldsForDownloading(true);
                updateActionLink(
                    actionLinkWrapper,
                    createCancelDownloadLink(
                        progressLabel,
                        actionLinkWrapper,
                        huggingFaceComboBoxModel,
                        progressIndicator));
              });
            },
            () -> SwingUtilities.invokeLater(() -> {
              configureFieldsForDownloading(false);
              updateActionLink(
                  actionLinkWrapper,
                  createDownloadModelLink(
                      progressLabel,
                      actionLinkWrapper,
                      huggingFaceComboBoxModel));
              actionLinkWrapper.setVisible(false);
              LlamaSettingsState.getInstance()
                  .setHuggingFaceModel(
                      (HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem());
            }),
            (error) -> {
              throw new RuntimeException(error);
            },
            (text) -> SwingUtilities.invokeLater(() -> progressLabel.setText(text)),
            huggingFaceComboBoxModel), "unknown");
  }

  private void updateModelHelpTooltip(HuggingFaceModel model) {
    helpIcon.setToolTipText(null);
    var llamaModel = LlamaModel.findByHuggingFaceModel(model);
    new HelpTooltip()
        .setTitle(llamaModel.getLabel())
        .setDescription("<html><p>" + llamaModel.getDescription() + "</p></html>")
        .setBrowserLink(
            CodeGPTBundle.get("settingsConfigurable.service.llama.linkToModel.label"),
            model.getHuggingFaceURL())
        .installOn(helpIcon);
  }

  static class ModelSize {

    private final int size;

    ModelSize(int size) {
      this.size = size;
    }

    int getSize() {
      return size;
    }

    @Override
    public String toString() {
      return size + "B";
    }
  }
}
