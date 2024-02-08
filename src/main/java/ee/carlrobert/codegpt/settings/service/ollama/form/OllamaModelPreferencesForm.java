package ee.carlrobert.codegpt.settings.service.ollama.form;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.settings.service.llama.form.AsyncProgressPanel;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettingsState;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.GetAvailableModelsAction;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.OllamaChildModel;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.OllamaModelParent;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.OllamaModelQuantizationComboBox;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.OllamaParentModelComboBox;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.compress.utils.Lists;

public class OllamaModelPreferencesForm {

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

  private final ComboBox<OllamaModelParent> parentModelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<OllamaChildModel> childModelComboBox;
  private final DefaultComboBoxModel<OllamaChildModel> childModelComboBoxModel;
  private final OllamaParentModelComboBox parentModelComboBoxModel;
  private final JBLabel helpIcon;
  private final AsyncProgressPanel updateModelListIcon;
  private final JPanel loadModelsActionWrapper;
  private final JBLabel progressLabel;
  private final JBLabel modelDetailsLabel;
  private final AnActionLink loadModelsLink;

  public OllamaModelPreferencesForm() {
    progressLabel = new JBLabel("");
    progressLabel.setBorder(JBUI.Borders.emptyLeft(2));
    progressLabel.setFont(JBUI.Fonts.smallFont());
    helpIcon = new JBLabel(General.ContextHelp);
    updateModelListIcon = new AsyncProgressPanel();
    childModelComboBoxModel = new DefaultComboBoxModel<>();

    loadModelsActionWrapper = new JPanel(new BorderLayout());
    loadModelsActionWrapper.setBorder(JBUI.Borders.emptyLeft(2));
    loadModelsLink = createLoadModelsLink();
    loadModelsActionWrapper.add(loadModelsLink, BorderLayout.WEST);
    modelDetailsLabel = new JBLabel();
    childModelComboBox = createHuggingFaceComboBox(childModelComboBoxModel,
        modelDetailsLabel);
    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    parentModelComboBoxModel = new OllamaParentModelComboBox();
    parentModelComboBox = createModelComboBox(parentModelComboBoxModel, modelSizeComboBoxModel);
    modelSizeComboBox = createModelSizeComboBox(
        parentModelComboBoxModel,
        modelSizeComboBoxModel,
        childModelComboBoxModel);
    setEnabled(false);
  }

  public JPanel getForm() {
    return createPredefinedModelForm();
  }

  public void resetForm(OllamaSettingsState state) {
    childModelComboBoxModel.setSelectedItem(state.getOllamaModel());
  }

  public OllamaChildModel getSelectedModel() {
    return (OllamaChildModel) childModelComboBoxModel.getSelectedItem();
  }

  private JPanel createPredefinedModelForm() {
    var quantizationHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.quantization.comment"),
        true);
    quantizationHelpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(parentModelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(8));
    modelComboBoxWrapper.add(helpIcon);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(updateModelListIcon);

    var huggingFaceModelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    huggingFaceModelComboBoxWrapper.add(childModelComboBox);
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
        .addComponentToRightColumn(loadModelsActionWrapper)
        .addComponentToRightColumn(progressLabel)
        .addVerticalGap(4)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private String getOllamaModelDetailsHtml(OllamaChildModel model) {
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

  private ComboBox<OllamaModelParent> createModelComboBox(
      OllamaParentModelComboBox ollamaParentModelComboBox,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel) {
    var comboBox = new ComboBox<>(ollamaParentModelComboBox);
    comboBox.setPreferredSize(new Dimension(280, comboBox.getPreferredSize().height));
    comboBox.addItemListener(e -> {
      var selectedModel = (OllamaModelParent) e.getItem();

      var modelSizes = selectedModel.getSortedUniqueModelSizes().stream()
          .map(ModelSize::new)
          .collect(toList());

      modelSizeComboBoxModel.removeAllElements();
      modelSizeComboBoxModel.addAll(modelSizes);
      modelSizeComboBoxModel.setSelectedItem(modelSizes.get(0));
      modelSizeComboBox.setEnabled(modelSizes.size() > 1);

      var huggingFaceModels = selectedModel.getChildrenModels().stream()
          .filter(model -> {
            var size = ((ModelSize) modelSizeComboBoxModel.getSelectedItem()).getSize();
            return size == model.getParameterSize();
          })
          .collect(toList());

      childModelComboBoxModel.removeAllElements();
      childModelComboBoxModel.addAll(huggingFaceModels);
      childModelComboBoxModel.setSelectedItem(huggingFaceModels.get(0));
    });
    return comboBox;
  }

  private ComboBox<ModelSize> createModelSizeComboBox(
      OllamaParentModelComboBox ollamaModelFamilyCheckBox,
      DefaultComboBoxModel<ModelSize> modelSizeComboBoxModel,
      DefaultComboBoxModel<OllamaChildModel> huggingFaceComboBoxModel) {
    var comboBox = new ComboBox<>(modelSizeComboBoxModel);
    comboBox.setPreferredSize(parentModelComboBox.getPreferredSize());
    comboBox.setSelectedItem(modelSizeComboBoxModel.getSelectedItem());
    comboBox.addItemListener(e -> {
      var selectedModelFamily = ollamaModelFamilyCheckBox.getSelectedItem();
      var models = selectedModelFamily.getChildrenModels().stream()
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

  private ComboBox<OllamaChildModel> createHuggingFaceComboBox(
      DefaultComboBoxModel<OllamaChildModel> ollamaComboBoxModel, JBLabel modelDetailsLabel) {
    var comboBox = new OllamaModelQuantizationComboBox(ollamaComboBoxModel);
    comboBox.addItemListener(e -> {
      var selectedModel = (OllamaChildModel) e.getItem();
      updateModelHelpTooltip(selectedModel);
      modelDetailsLabel.setText(getOllamaModelDetailsHtml(selectedModel));
    });
    return comboBox;
  }

  private AnActionLink createLoadModelsLink() {
    return new AnActionLink(
        CodeGPTBundle.get("settingsConfigurable.service.ollama.updateModelsLink.label"),
        new GetAvailableModelsAction(
            () -> SwingUtilities.invokeLater(() -> {
              showModelUpdateHint(true);
            }),
            this::updateAvailableModels,
            (error) -> {
              SwingUtilities.invokeLater(() -> {
                showModelUpdateHint(false);
                OverlayUtil.showBalloon(
                    CodeGPTBundle.get("validation.error.serverUnreachable"),
                    MessageType.ERROR,
                    loadModelsLink);
              });
            }));
  }

  public void refreshAvailableModels() {
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      SwingUtilities.invokeLater(() -> showModelUpdateHint(true));
      try {
        updateAvailableModels(CompletionClientProvider.getOllamaClient().getModelTags()
            .getModels());
      } finally {
        SwingUtilities.invokeLater(() -> showModelUpdateHint(false));
      }
    });
  }

  private void updateAvailableModels(List<OllamaModel> ollamaModelList) {
    SwingUtilities.invokeLater(() -> {
      showModelUpdateHint(false);
      if (ollamaModelList.isEmpty()) {
        setEnabled(false);
        parentModelComboBoxModel.updateList(Lists.newArrayList());
        OverlayUtil.showBalloon(
            CodeGPTBundle.get("validation.error.noModelsAvailable"),
            MessageType.ERROR,
            loadModelsLink);
      } else {
        parentModelComboBoxModel.updateList(mapOllamaModelsToModelFamilies(ollamaModelList));
      }
    });
  }

  private List<OllamaModelParent> mapOllamaModelsToModelFamilies(
      List<OllamaModel> ollamaModelList) {
    return ollamaModelList.stream()
        .map(ollamaModel -> new OllamaModelParent(ollamaModel, ollamaModelList))
        .distinct().collect(toList());
  }

  private void updateModelHelpTooltip(OllamaChildModel model) {
    helpIcon.setToolTipText(null);
    new HelpTooltip()
        .setTitle(model.getTag())
        .setDescription("")
        .setBrowserLink(
            CodeGPTBundle.get("settingsConfigurable.service.llama.linkToModel.label"),
            model.getModelLink())
        .installOn(helpIcon);
  }

  private void setEnabled(boolean enabled) {
    parentModelComboBox.setEnabled(enabled);
    modelSizeComboBox.setEnabled(enabled);
    childModelComboBox.setEnabled(enabled);
  }

  private void showModelUpdateHint(boolean show) {
    if (show) {
      setEnabled(false);
      helpIcon.setVisible(false);
      updateModelListIcon.updateText("Loading available models...");
      updateModelListIcon.setVisible(true);
    } else {
      setEnabled(true);
      updateModelListIcon.setVisible(false);
      updateModelListIcon.updateText("");
      helpIcon.setVisible(true);
    }
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