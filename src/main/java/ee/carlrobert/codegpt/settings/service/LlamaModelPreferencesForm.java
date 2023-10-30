package ee.carlrobert.codegpt.settings.service;

import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.settings.DownloadModelAction;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LlamaModelPreferencesForm {

  private final TextFieldWithBrowseButton customModelPathBrowserButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final ComboBox<PromptTemplate> promptTemplateComboBox;
  private final JBLabel modelExistsIcon;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;
  private final JBCheckBox useCustomModelCheckBox;
  private final EnumComboBoxModel<LlamaModel> modelComboBoxModel;
  private final JBLabel helpIcon;
  private final JPanel downloadModelActionLinkWrapper;
  private final JBLabel progressLabel;

  private @Nullable Consumer<HuggingFaceModel> huggingFaceModelChangeListener;
  private @Nullable Consumer<Boolean> useCustomModelChangeListener;
  private @Nullable Runnable downloadSuccessListener;

  public LlamaModelPreferencesForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    customModelPathBrowserButton = createCustomModelPathBrowseButton(
        LlamaSettingsState.getInstance().isUseCustomModel());
    progressLabel = new JBLabel("");
    modelExistsIcon = new JBLabel(Actions.Commit);
    modelExistsIcon.setVisible(isModelExists(llamaSettings.getHuggingFaceModel()));
    helpIcon = new JBLabel(General.ContextHelp);
    huggingFaceComboBoxModel = new DefaultComboBoxModel<>();
    var llm = llamaSettings.getHuggingFaceModel();
    var llamaModel = LlamaModel.findByHuggingFaceModel(llm);
    var selectableModels = llamaModel.getHuggingFaceModels().stream()
        .filter(model -> model.getParameterSize() == llm.getParameterSize())
        .collect(toList());
    huggingFaceComboBoxModel.addAll(selectableModels);
    downloadModelActionLinkWrapper = new JPanel(new BorderLayout());
    downloadModelActionLinkWrapper.add(
        createDownloadModelLink(
            progressLabel,
            downloadModelActionLinkWrapper,
            huggingFaceComboBoxModel),
        BorderLayout.WEST);
    huggingFaceModelComboBox = createHuggingFaceComboBox(
        huggingFaceComboBoxModel,
        modelExistsIcon,
        downloadModelActionLinkWrapper);
    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    var initialModelSizes = llamaModel.getHuggingFaceModels().stream()
        .filter(distinctByKey(HuggingFaceModel::getParameterSize))
        .map(model -> new ModelSize(model.getParameterSize()))
        .collect(toList());
    modelSizeComboBoxModel.addAll(initialModelSizes);
    modelComboBoxModel = new EnumComboBoxModel<>(LlamaModel.class);
    modelComboBox = createModelComboBox(modelComboBoxModel, llamaModel, modelSizeComboBoxModel);
    modelSizeComboBox = createModelSizeComboBox(
        modelSizeComboBoxModel,
        huggingFaceComboBoxModel,
        initialModelSizes);
    promptTemplateComboBox = new ComboBox<>(new EnumComboBoxModel<>(PromptTemplate.class));
    promptTemplateComboBox.setEnabled(llamaSettings.isUseCustomModel());
    useCustomModelCheckBox = new JBCheckBox("Use custom model");
    useCustomModelCheckBox.addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      customModelPathBrowserButton.setEnabled(selected);
      promptTemplateComboBox.setEnabled(selected);
      modelComboBox.setEnabled(!selected);
      modelSizeComboBox.setEnabled((!selected));
      huggingFaceModelComboBox.setEnabled((!selected));
      if (useCustomModelChangeListener != null) {
        useCustomModelChangeListener.accept(selected);
      }
    });
  }

  public JPanel getForm() {
    var helpText = ComponentPanelBuilder.createCommentComponent(
        "Only .gguf files are supported",
        true);
    helpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(8));
    modelComboBoxWrapper.add(helpIcon);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    modelSizeComboBox.setPreferredSize(modelComboBox.getPreferredSize());
    huggingFaceModelComboBox.setPreferredSize(modelComboBox.getPreferredSize());

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addLabeledComponent("Model size:", modelSizeComboBox)
        .addLabeledComponent("Quantization:", huggingFaceModelComboBox)
        .addComponentToRightColumn(downloadModelActionLinkWrapper)
        .addComponentToRightColumn(progressLabel)
        .addVerticalGap(16)
        .addComponent(useCustomModelCheckBox)
        .addComponent(customModelPathBrowserButton)
        .addComponent(helpText)
        .addVerticalGap(4)
        .addLabeledComponent("Prompt template:", promptTemplateComboBox)
        .getPanel();
  }

  public void addHuggingFaceModelChangeListener(
      Consumer<HuggingFaceModel> huggingFaceModelChangeListener) {
    this.huggingFaceModelChangeListener = huggingFaceModelChangeListener;
  }

  public void addUseCustomModelChangeListener(Consumer<Boolean> useCustomModelChangeListener) {
    this.useCustomModelChangeListener = useCustomModelChangeListener;
  }

  public void addDownloadSuccessListener(Runnable downloadSuccessListener) {
    this.downloadSuccessListener = downloadSuccessListener;
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
    comboBox.setPreferredSize(new Dimension(200, comboBox.getPreferredSize().height));
    comboBox.setSelectedItem(llamaModel);
    comboBox.addItemListener(e -> {
      var selectedModel = llamaModelEnumComboBoxModel.getSelectedItem();
      var modelSizes = selectedModel.getHuggingFaceModels().stream()
          .filter(distinctByKey(HuggingFaceModel::getParameterSize))
          .map(item -> new ModelSize(item.getParameterSize()))
          .collect(toList());

      modelSizeComboBoxModel.removeAllElements();
      modelSizeComboBoxModel.addAll(modelSizes);
      modelSizeComboBoxModel.setSelectedItem(modelSizes.get(0));

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
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel,
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel,
      List<ModelSize> initialModelSizes) {
    var comboBox = new ComboBox<>(modelSizeComboBoxModel);
    comboBox.setSelectedItem(initialModelSizes.get(0));
    comboBox.addItemListener(e -> {
      var selectedModel = modelComboBoxModel.getSelectedItem();
      var models = selectedModel.getHuggingFaceModels().stream()
          .filter(model -> {
            var selectedModelSize = (ModelSize) modelSizeComboBoxModel.getSelectedItem();
            return selectedModelSize != null &&
                selectedModelSize.getSize() == model.getParameterSize();
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
      JPanel downloadModelActionLinkWrapper) {
    var comboBox = new ComboBox<>(huggingFaceComboBoxModel);
    comboBox.addItemListener(e -> {
      var selectedModel = (HuggingFaceModel) e.getItem();
      var modelExists = isModelExists(selectedModel);

      updateModelHelpTooltip(selectedModel);
      modelExistsIcon.setVisible(modelExists);
      downloadModelActionLinkWrapper.setVisible(!modelExists);
      if (huggingFaceModelChangeListener != null) {
        huggingFaceModelChangeListener.accept(selectedModel);
      }
    });
    return comboBox;
  }

  private TextFieldWithBrowseButton createCustomModelPathBrowseButton(boolean enabled) {
    var browseButton = new TextFieldWithBrowseButton();
    browseButton.setEnabled(enabled);
    browseButton.setText(CodeGPTPlugin.getLlamaModelsPath());

    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
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
    return new AnActionLink("Cancel Downloading", new AnAction() {
      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        SwingUtilities.invokeLater(() -> {
          configureFieldsForDownloading(false);
          updateActionLink(
              actionLinkWrapper,
              createDownloadModelLink(progressLabel, actionLinkWrapper, huggingFaceComboBoxModel));
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
    return new AnActionLink("Download Model", new DownloadModelAction(
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
              createDownloadModelLink(progressLabel, actionLinkWrapper, huggingFaceComboBoxModel));
          actionLinkWrapper.setVisible(false);
          LlamaSettingsState.getInstance()
              .setHuggingFaceModel((HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem());
          if (downloadSuccessListener != null) {
            downloadSuccessListener.run();
          }
        }),
        (error) -> {
          throw new RuntimeException(error);
        },
        (text) -> SwingUtilities.invokeLater(() -> progressLabel.setText(text)),
        huggingFaceComboBoxModel), "unknown");
  }

  private void updateModelHelpTooltip(HuggingFaceModel model) {
    helpIcon.setToolTipText(null);
    new HelpTooltip()
        .setTitle(LlamaModel.findByHuggingFaceModel(model).getLabel())
        .setDescription("<html><p>" + model.getFileName() + "</p></html>")
        .setBrowserLink("Link to model", model.getFileURL())
        .installOn(helpIcon);
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    return t -> ConcurrentHashMap.newKeySet().add(keyExtractor.apply(t));
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
