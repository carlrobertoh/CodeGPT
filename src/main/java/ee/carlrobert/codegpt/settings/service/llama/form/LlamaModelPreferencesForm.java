package ee.carlrobert.codegpt.settings.service.llama.form;

import static ee.carlrobert.codegpt.settings.service.llama.LlamaSettings.getLlamaModelsPath;
import static ee.carlrobert.codegpt.settings.service.llama.LlamaSettings.isModelExists;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

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
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel.ModelSize;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class LlamaModelPreferencesForm {

  private static final String PREDEFINED_MODEL_FORM_CARD_CODE = "PredefinedModelSettings";
  private static final String CUSTOM_MODEL_FORM_CARD_CODE = "CustomModelSettings";
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

  private final TextFieldWithBrowseButton browsableCustomModelTextField;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final JBLabel modelExistsIcon;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;
  private final JBLabel helpIcon;
  private final JPanel downloadModelActionLinkWrapper;
  private final JBLabel progressLabel;
  private final JBLabel modelDetailsLabel;
  private final CardLayout cardLayout;
  private final JBRadioButton predefinedModelRadioButton;
  private final JBRadioButton customModelRadioButton;
  private final ChatPromptTemplatePanel localPromptTemplatePanel;
  private final InfillPromptTemplatePanel infillPromptTemplatePanel;

  public LlamaModelPreferencesForm() {
    cardLayout = new CardLayout();
    progressLabel = new JBLabel("");
    progressLabel.setBorder(JBUI.Borders.emptyLeft(2));
    progressLabel.setFont(JBUI.Fonts.smallFont());
    modelExistsIcon = new JBLabel(Actions.Checked);
    var llamaSettings = LlamaSettings.getCurrentState();
    modelExistsIcon.setVisible(isModelExists(llamaSettings.getHuggingFaceModel()));
    helpIcon = new JBLabel(General.ContextHelp);
    huggingFaceComboBoxModel = new DefaultComboBoxModel<>();
    var llm = llamaSettings.getHuggingFaceModel();
    var llamaModel = LlamaModel.findByHuggingFaceModel(llm);

    var selectableModels = llamaModel.getHuggingFaceModels().stream()
        .filter(model -> model.getParameterSize() == llm.getParameterSize())
        .toList();
    huggingFaceComboBoxModel.addAll(selectableModels);
    huggingFaceComboBoxModel.setSelectedItem(llm);
    downloadModelActionLinkWrapper = new JPanel(new BorderLayout());
    downloadModelActionLinkWrapper.setBorder(JBUI.Borders.emptyLeft(2));
    downloadModelActionLinkWrapper.add(
        createDownloadModelLink(
            progressLabel,
            downloadModelActionLinkWrapper,
            huggingFaceComboBoxModel),
        BorderLayout.WEST);
    modelDetailsLabel = new JBLabel();
    huggingFaceModelComboBox = createModelQuantizationComboBox(huggingFaceComboBoxModel);
    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    huggingFaceModelComboBox.setEnabled(!llamaServerAgent.isServerRunning());
    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    var modelComboBoxModel = new DefaultComboBoxModel<LlamaModel>();
    modelComboBoxModel.addAll(LlamaModel.getSorted());
    modelComboBox = createModelComboBox(
        modelComboBoxModel, llamaModel, llm, llamaServerAgent, modelSizeComboBoxModel);
    modelComboBox.setEnabled(!llamaServerAgent.isServerRunning());
    modelSizeComboBox = createModelSizeComboBox(
        modelComboBoxModel,
        modelSizeComboBoxModel,
        llamaServerAgent,
        huggingFaceComboBoxModel);
    browsableCustomModelTextField = createBrowsableCustomModelTextField(
        !llamaServerAgent.isServerRunning());
    browsableCustomModelTextField.setText(llamaSettings.getCustomLlamaModelPath());
    localPromptTemplatePanel = new ChatPromptTemplatePanel(
        llamaSettings.getLocalModelPromptTemplate(),
        !llamaServerAgent.isServerRunning());
    predefinedModelRadioButton = new JBRadioButton("Use pre-defined model",
        !llamaSettings.isUseCustomModel());
    customModelRadioButton = new JBRadioButton("Use custom model",
        llamaSettings.isUseCustomModel());
    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        llamaSettings.getLocalModelInfillPromptTemplate(),
        !llamaServerAgent.isServerRunning()
    );
  }

  public JPanel getForm() {
    JPanel finalPanel = new JPanel(new BorderLayout());
    finalPanel.add(createRadioButtonsPanel(), BorderLayout.NORTH);
    finalPanel.add(createFormPanelCards(), BorderLayout.CENTER);
    return finalPanel;
  }

  public void resetForm(LlamaSettingsState state) {
    huggingFaceComboBoxModel.setSelectedItem(state.getHuggingFaceModel());
    browsableCustomModelTextField.setText(state.getCustomLlamaModelPath());
    customModelRadioButton.setSelected(state.isUseCustomModel());
    localPromptTemplatePanel.setPromptTemplate(state.getLocalModelPromptTemplate());
    infillPromptTemplatePanel.setPromptTemplate(state.getLocalModelInfillPromptTemplate());
  }

  public void enableFields(boolean enabled) {
    modelComboBox.setEnabled(enabled);
    modelSizeComboBox.setEnabled(enabled);
    huggingFaceModelComboBox.setEnabled(enabled);
  }

  public TextFieldWithBrowseButton getBrowsableCustomModelTextField() {
    return browsableCustomModelTextField;
  }

  public ComboBox<HuggingFaceModel> getHuggingFaceModelComboBox() {
    return huggingFaceModelComboBox;
  }

  public HuggingFaceModel getSelectedModel() {
    return (HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem();
  }

  public String getCustomLlamaModelPath() {
    return browsableCustomModelTextField.getText();
  }

  public boolean isUseCustomLlamaModel() {
    return customModelRadioButton.isSelected();
  }

  public PromptTemplate getPromptTemplate() {
    return localPromptTemplatePanel.getPromptTemplate();
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return infillPromptTemplatePanel.getPromptTemplate();
  }

  public String getActualModelPath() {
    return isUseCustomLlamaModel()
        ? getCustomLlamaModelPath()
        : getLlamaModelsPath().resolve(getSelectedModel().getFileName()).toString();
  }

  private JPanel createFormPanelCards() {
    var formPanelCards = new JPanel(cardLayout);
    formPanelCards.setBorder(JBUI.Borders.emptyLeft(16));
    formPanelCards.add(createPredefinedModelForm(), PREDEFINED_MODEL_FORM_CARD_CODE);
    formPanelCards.add(createCustomModelForm(), CUSTOM_MODEL_FORM_CARD_CODE);
    cardLayout.show(
        formPanelCards,
        predefinedModelRadioButton.isSelected()
            ? PREDEFINED_MODEL_FORM_CARD_CODE
            : CUSTOM_MODEL_FORM_CARD_CODE);

    predefinedModelRadioButton.addActionListener(e ->
        cardLayout.show(formPanelCards, PREDEFINED_MODEL_FORM_CARD_CODE));
    customModelRadioButton.addActionListener(e ->
        cardLayout.show(formPanelCards, CUSTOM_MODEL_FORM_CARD_CODE));

    return formPanelCards;
  }

  private JPanel createRadioButtonsPanel() {
    var buttonGroup = new ButtonGroup();
    buttonGroup.add(predefinedModelRadioButton);
    buttonGroup.add(customModelRadioButton);

    var radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    radioPanel.add(predefinedModelRadioButton);
    radioPanel.add(Box.createVerticalStrut(4));
    radioPanel.add(customModelRadioButton);
    radioPanel.add(Box.createVerticalStrut(8));
    return radioPanel;
  }

  private JPanel createCustomModelForm() {
    var customModelHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.comment"),
        true);
    customModelHelpText.setBorder(JBUI.Borders.empty(0, 4));

    return FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.label"),
            browsableCustomModelTextField)
        .addComponentToRightColumn(customModelHelpText)
        .addLabeledComponent(CodeGPTBundle.get("shared.promptTemplate"), localPromptTemplatePanel)
        .addComponentToRightColumn(localPromptTemplatePanel.getPromptTemplateHelpText())
        .addLabeledComponent(CodeGPTBundle.get("shared.infillPromptTemplate"),
            infillPromptTemplatePanel)
        .addComponentToRightColumn(infillPromptTemplatePanel.getPromptTemplateHelpText())
        .addVerticalGap(4)
        .addComponentFillVertically(new JPanel(), 0)
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
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private String getHuggingFaceModelDetailsHtml(HuggingFaceModel model) {
    int parameterSize = model.getParameterSize();
    int quantization = model.getQuantization();

    var details = modelDetailsMap.getOrDefault(parameterSize, emptyMap()).get(quantization);
    if (details == null && model.getDownloadSize() == null) {
      return "";
    }
    if (details == null) {
      return format("<html>"
          + "<p style=\"margin: 0\"><small>File Size: <strong>%.2f GB</strong></small></p>"
          + "</html>", model.getDownloadSize());
    }
    return format("<html>"
        + "<p style=\"margin: 0\"><small>File Size: <strong>%.2f GB</strong></small></p>"
        + "<p style=\"margin: 0\"><small>Max RAM Required: <strong>%.2f GB</strong></small></p>"
        + "</html>", details.fileSize, details.maxRAMRequired);
  }

  private ComboBox<LlamaModel> createModelComboBox(
      ComboBoxModel<LlamaModel> llamaModelEnumComboBoxModel,
      LlamaModel llamaModel,
      HuggingFaceModel llm,
      LlamaServerAgent llamaServerAgent,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel) {
    var comboBox = new ComboBox<>(llamaModelEnumComboBoxModel);
    comboBox.setPreferredSize(new Dimension(280, comboBox.getPreferredSize().height));
    comboBox.setSelectedItem(llamaModel);
    initializeModelSizes(llamaModel, llm, modelSizeComboBoxModel);
    comboBox.addItemListener(e -> {
      var selectedModel = (LlamaModel) e.getItem();
      var hfm = selectedModel.getLastExistingModelOrFirst();
      var modelSize = initializeModelSizes(selectedModel, hfm, modelSizeComboBoxModel);
      var huggingFaceModels = selectedModel.filterSelectedModelsBySize(modelSize);
      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(huggingFaceModels);
      huggingFaceComboBoxModel.setSelectedItem(hfm);
      modelSizeComboBox.setEnabled(
          modelSizeComboBox.getModel().getSize() > 1 && !llamaServerAgent.isServerRunning());
    });
    return comboBox;
  }

  private static ModelSize initializeModelSizes(
      LlamaModel llamaModel,
      HuggingFaceModel hfm,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel) {
    var modelSizes = llamaModel.getSortedUniqueModelSizes();
    modelSizeComboBoxModel.removeAllElements();
    modelSizeComboBoxModel.addAll(modelSizes);
    var selectedModelSize = modelSizes.stream()
        .filter(ms -> ms.size() == hfm.getParameterSize())
        .findFirst().orElse(modelSizes.get(0));
    modelSizeComboBoxModel.setSelectedItem(selectedModelSize);
    return selectedModelSize;
  }

  private ComboBox<ModelSize> createModelSizeComboBox(
      ComboBoxModel<LlamaModel> llamaModelComboBoxModel,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel,
      LlamaServerAgent llamaServerAgent,
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel) {
    var comboBox = new ComboBox<>(modelSizeComboBoxModel);
    comboBox.setPreferredSize(modelComboBox.getPreferredSize());
    comboBox.setSelectedItem(modelSizeComboBoxModel.getSelectedItem());
    comboBox.setEnabled(
        modelSizeComboBoxModel.getSize() > 1 && !llamaServerAgent.isServerRunning());
    comboBox.addItemListener(e -> {
      var selectedModel = (LlamaModel) llamaModelComboBoxModel.getSelectedItem();
      var models = selectedModel.filterSelectedModelsBySize(
              (ModelSize) modelSizeComboBoxModel.getSelectedItem());
      comboBox.setEnabled(
          modelSizeComboBoxModel.getSize() > 1 && !llamaServerAgent.isServerRunning());
      if (!models.isEmpty()) {
        huggingFaceComboBoxModel.removeAllElements();
        huggingFaceComboBoxModel.addAll(models);
        huggingFaceComboBoxModel.setSelectedItem(models.get(0));
      }
    });
    return comboBox;
  }

  private ComboBox<HuggingFaceModel> createModelQuantizationComboBox(
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel) {
    var comboBox = new ComboBox<>(huggingFaceComboBoxModel);
    updateFromModelState(comboBox.getItem());
    comboBox.addItemListener(e -> updateFromModelState((HuggingFaceModel) e.getItem()));
    comboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        var item = value instanceof HuggingFaceModel hfm ? hfm.getQuantizationLabel() : value;
        return super.getListCellRendererComponent(list, item, index, isSelected, cellHasFocus);
      }
    });
    return comboBox;
  }

  private void updateFromModelState(HuggingFaceModel selectedModel) {
    var modelExists = isModelExists(selectedModel);
    updateModelHelpTooltip(selectedModel);
    modelDetailsLabel.setText(getHuggingFaceModelDetailsHtml(selectedModel));
    modelExistsIcon.setVisible(modelExists);
    downloadModelActionLinkWrapper.setVisible(!modelExists);
  }

  private TextFieldWithBrowseButton createBrowsableCustomModelTextField(boolean enabled) {
    var browseButton = new TextFieldWithBrowseButton();
    browseButton.setEnabled(enabled);

    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    fileChooserDescriptor.setHideIgnored(false);
    browseButton.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor));
    return browseButton;
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
            progressIndicator -> SwingUtilities.invokeLater(() -> {
              configureFieldsForDownloading(true);
              updateActionLink(
                  actionLinkWrapper,
                  createCancelDownloadLink(
                      progressLabel,
                      actionLinkWrapper,
                      huggingFaceComboBoxModel,
                      progressIndicator));
            }),
            () -> SwingUtilities.invokeLater(() -> {
              configureFieldsForDownloading(false);
              updateActionLink(
                  actionLinkWrapper,
                  createDownloadModelLink(
                      progressLabel,
                      actionLinkWrapper,
                      huggingFaceComboBoxModel));
              actionLinkWrapper.setVisible(false);
              LlamaSettings.getCurrentState().setHuggingFaceModel(
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
        .setDescription(llamaModel.getDescription())
        .setBrowserLink(
            CodeGPTBundle.get("settingsConfigurable.service.llama.linkToModel.label"),
            model.getHuggingFaceURL())
        .installOn(helpIcon);
  }

  private record ModelDetails(double fileSize, double maxRAMRequired) {
  }
}
