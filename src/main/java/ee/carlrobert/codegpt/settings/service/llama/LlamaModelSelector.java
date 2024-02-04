package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createComment;
import static ee.carlrobert.codegpt.ui.UIUtil.createForm;
import static ee.carlrobert.codegpt.ui.UIUtil.createTextFieldWithBrowseButton;
import static java.lang.String.format;
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
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.CustomLlamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.service.util.ModelSelector;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.ui.ChatPromptTemplatePanel;
import ee.carlrobert.codegpt.ui.InfillPromptTemplatePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

/**
 * {@link ModelSelector} with either {@link HuggingFaceModel} selection or {@link CustomLlamaModel}
 * file chooser.
 */
public class LlamaModelSelector implements ModelSelector<LlamaCompletionModel> {

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

  private final JBRadioButton predefinedModelRadioButton;
  private final JBRadioButton customModelRadioButton;
  private final JBLabel helpIcon;
  private final JBLabel modelExistsIcon;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final JBLabel modelDetailsLabel;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final JPanel downloadModelActionLinkWrapper;
  private final JBLabel progressLabel;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;

  private final TextFieldWithBrowseButton customModelPathField;
  private final InfillPromptTemplatePanel infillPromptTemplatePanel;
  private final ChatPromptTemplatePanel chatPromptTemplateField;

  public LlamaModelSelector(LlamaCompletionModel initialModel) {
    boolean isCustomModel = initialModel instanceof CustomLlamaModel;
    predefinedModelRadioButton = new JBRadioButton("Use pre-defined model",
        !isCustomModel);
    customModelRadioButton = new JBRadioButton("Use custom model",
        isCustomModel);

    huggingFaceComboBoxModel = new DefaultComboBoxModel<>();
    var llm = isCustomModel ? LlamaModel.CODE_LLAMA.getHuggingFaceModels().get(0)
        : (HuggingFaceModel) initialModel;
    var llamaModel = isCustomModel ? LlamaModel.CODE_LLAMA
        : LlamaModel.findByHuggingFaceModel(llm);

    var selectableModels = llamaModel.getHuggingFaceModels().stream()
        .filter(model -> model.getParameterSize() == llm.getParameterSize())
        .collect(toList());
    huggingFaceComboBoxModel.addAll(selectableModels);
    progressLabel = new JBLabel("");
    progressLabel.setBorder(JBUI.Borders.emptyLeft(2));
    progressLabel.setFont(JBUI.Fonts.smallFont());
    modelExistsIcon = new JBLabel(Actions.Checked);
    modelExistsIcon.setVisible(LlamaCompletionModel.isModelExists(initialModel));
    helpIcon = new JBLabel(General.ContextHelp);

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
    huggingFaceComboBoxModel.setSelectedItem(llm);
    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
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

    String modelPath =
        initialModel instanceof CustomLlamaModel ? ((CustomLlamaModel) initialModel).getModelPath()
            : "";
    customModelPathField = createTextFieldWithBrowseButton(
        FileChooserDescriptorFactory.createSingleFileDescriptor("gguf"));
    customModelPathField.setText(modelPath);

    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        LlamaSettings.getInstance().getRemoteSettings().getInfillPromptTemplate(), true);
    chatPromptTemplateField = new ChatPromptTemplatePanel(
        LlamaSettings.getInstance().getRemoteSettings().getChatPromptTemplate(), true);
  }


  @Override
  public JComponent getComponent() {
    return createForm(predefinedModelRadioButton, createPredefinedModelForm(),
        customModelRadioButton, createCustomModelForm(), predefinedModelRadioButton.isSelected());
  }

  public void enableFields(boolean enabled) {
    modelComboBox.setEnabled(enabled);
    modelSizeComboBox.setEnabled(enabled);
    huggingFaceModelComboBox.setEnabled(enabled);
    chatPromptTemplateField.setEnabled(enabled);
  }

  private JPanel createCustomModelForm() {
    var customModelHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.comment"),
        true);
    customModelHelpText.setBorder(JBUI.Borders.empty(0, 4));

    return FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.label"),
            customModelPathField)
        .addVerticalGap(4)
        .addLabeledComponent(CodeGPTBundle.get("shared.infillPromptTemplate"),
            infillPromptTemplatePanel)
        .addLabeledComponent(CodeGPTBundle.get("shared.promptTemplate"), chatPromptTemplateField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.promptTemplate.comment"))
        .getPanel();
  }

  private JPanel createPredefinedModelForm() {
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
        .addVerticalGap(4)
        .getPanel();
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

    return format("<html>"
        + "<p style=\"margin: 0\"><small>File Size: <strong>%.2f GB</strong></small></p>"
        + "<p style=\"margin: 0\"><small>Max RAM Required: <strong>%.2f GB</strong></small></p>"
        + "</html>", details.fileSize, details.maxRAMRequired);
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
      var modelExists = LlamaCompletionModel.isModelExists(selectedModel);

      updateModelHelpTooltip(selectedModel);
      modelDetailsLabel.setText(getHuggingFaceModelDetailsHtml(selectedModel));
      modelExistsIcon.setVisible(modelExists);
      downloadModelActionLinkWrapper.setVisible(!modelExists);
    });
    return comboBox;
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
        .setDescription(llamaModel.getDescription())
        .setBrowserLink(
            CodeGPTBundle.get("settingsConfigurable.service.llama.linkToModel.label"),
            model.getModelUrl())
        .installOn(helpIcon);
  }

  public boolean isModelExists() {
    return LlamaCompletionModel.isModelExists(getSelectedModel());
  }

  public void setUseCustomModel(boolean isUseCustomModel) {
    customModelRadioButton.setSelected(isUseCustomModel);
  }

  public boolean isUseCustomModel() {
    return customModelRadioButton.isSelected();
  }

  public ComboBox<HuggingFaceModel> getHuggingFaceModelComboBox() {
    return huggingFaceModelComboBox;
  }

  @Override
  public void setSelectedModel(LlamaCompletionModel model) {
    if (model instanceof HuggingFaceModel) {
      setUseCustomModel(false);
      huggingFaceComboBoxModel.setSelectedItem(model);
    } else {
      setUseCustomModel(true);
      setCustomModel(((CustomLlamaModel) model).getModelPath());
    }
  }

  @Override
  public LlamaCompletionModel getSelectedModel() {
    if (!isUseCustomModel()) {
      return (HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem();
    } else {
      return new CustomLlamaModel(getCustomModel());
    }
  }

  public void setInfillPromptTemplate(InfillPromptTemplate infillPromptTemplate) {
    infillPromptTemplatePanel.setPromptTemplate(infillPromptTemplate);
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return isUseCustomModel() ? infillPromptTemplatePanel.getPromptTemplate()
        : LlamaModel.findByHuggingFaceModel((HuggingFaceModel) getSelectedModel())
            .getInfillPromptTemplate();
  }

  public void setChatPromptTemplate(PromptTemplate chatPromptTemplate) {
    chatPromptTemplateField.setPromptTemplate(chatPromptTemplate);
  }

  public PromptTemplate getChatPromptTemplate() {
    return isUseCustomModel() ? chatPromptTemplateField.getPromptTemplate()
        : LlamaModel.findByHuggingFaceModel((HuggingFaceModel) getSelectedModel())
            .getPromptTemplate();
  }

  public TextFieldWithBrowseButton getCustomModelPathField() {
    return customModelPathField;
  }

  private void setCustomModel(String customModel) {
    customModelPathField.setText(customModel);
  }

  private String getCustomModel() {
    return customModelPathField.getText();
  }

  private static class ModelDetails {

    double fileSize;
    double maxRAMRequired;

    public ModelDetails(double fileSize, double maxRAMRequired) {
      this.fileSize = fileSize;
      this.maxRAMRequired = maxRAMRequired;
    }
  }

  private static class ModelSize {

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
