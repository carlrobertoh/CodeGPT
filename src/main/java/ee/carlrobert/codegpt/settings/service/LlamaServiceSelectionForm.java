package ee.carlrobert.codegpt.settings.service;

import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.settings.DownloadModelAction;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LlamaServiceSelectionForm extends JPanel {

  private final TextFieldWithBrowseButton textFieldWithBrowseButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final ComboBox<PromptTemplate> promptTemplateComboBox;
  private final BorderLayoutPanel downloadModelLinkWrapper;
  private final JBLabel modelExistsIcon;
  private final PortField portField;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;
  private final JBCheckBox useCustomModelCheckBox;
  private final JPanel serverProgressPanel;
  private final EnumComboBoxModel<LlamaModel> modelComboBoxModel;
  private JBLabel helpIcon;

  public LlamaServiceSelectionForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    var llm = llamaSettings.getHuggingFaceModel();
    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    textFieldWithBrowseButton.setEnabled(llamaSettings.isUseCustomModel());
    textFieldWithBrowseButton.setText(CodeGPTPlugin.getLlamaModelsPath());
    textFieldWithBrowseButton.addBrowseFolderListener(
        new TextBrowseFolderListener(fileChooserDescriptor));

    downloadModelLinkWrapper = JBUI.Panels.simplePanel().withBorder(JBUI.Borders.emptyLeft(2));
    modelExistsIcon = new JBLabel(Actions.Commit);
    modelExistsIcon.setVisible(isModelExists(llamaSettings.getHuggingFaceModel()));

    huggingFaceComboBoxModel = new DefaultComboBoxModel<>();
    var llamaModel = LlamaModel.findByHuggingFaceModel(llm);
    var selectableModels = llamaModel.getHuggingFaceModels().stream()
        .filter(model -> model.getParameterSize() == llm.getParameterSize())
        .collect(toList());
    huggingFaceComboBoxModel.addAll(selectableModels);
    huggingFaceModelComboBox = new ComboBox<>(huggingFaceComboBoxModel);
    huggingFaceModelComboBox.addItemListener(e -> {
      var selectedModel = (HuggingFaceModel) e.getItem();
      var modelExists = isModelExists(selectedModel);

      updateModelHelpTooltip(selectedModel);
      modelExistsIcon.setVisible(modelExists);
      downloadModelLinkWrapper.setVisible(!modelExists);
    });

    helpIcon = new JBLabel(General.ContextHelp);

    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    var initialModelSizes = llamaModel.getHuggingFaceModels().stream()
        .filter(distinctByKey(HuggingFaceModel::getParameterSize))
        .map(model -> new ModelSize(model.getParameterSize()))
        .collect(toList());
    modelSizeComboBoxModel.addAll(initialModelSizes);

    modelComboBoxModel = new EnumComboBoxModel<>(LlamaModel.class);
    modelComboBox = new ComboBox<>(modelComboBoxModel);
    modelComboBox.setSelectedItem(llamaModel);
    modelComboBox.addItemListener(e -> {
      var selectedModel = modelComboBoxModel.getSelectedItem();
      var modelSizes = selectedModel.getHuggingFaceModels().stream()
          .filter(distinctByKey(HuggingFaceModel::getParameterSize))
          .map(item -> new ModelSize(item.getParameterSize()))
          .collect(toList());

      modelSizeComboBoxModel.removeAllElements();
      modelSizeComboBoxModel.addAll(modelSizes);
      modelSizeComboBoxModel.setSelectedItem(modelSizes.get(0));

      var models = selectedModel.getHuggingFaceModels().stream()
          .filter(model -> {
            var size = ((ModelSize) modelSizeComboBoxModel.getSelectedItem()).getSize();
            return size == model.getParameterSize();
          })
          .collect(toList());

      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(models);
      huggingFaceComboBoxModel.setSelectedItem(models.get(0));
    });

    modelSizeComboBox = new ComboBox<>(modelSizeComboBoxModel);
    modelSizeComboBox.setSelectedItem(initialModelSizes.get(0));
    modelSizeComboBox.addItemListener(e -> {
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

    portField = new PortField(llamaSettings.getServerPort());

    promptTemplateComboBox = new ComboBox<>(new EnumComboBoxModel<>(PromptTemplate.class));
    promptTemplateComboBox.setEnabled(llamaSettings.isUseCustomModel());

    useCustomModelCheckBox = new JBCheckBox("Use custom model");
    useCustomModelCheckBox.addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      textFieldWithBrowseButton.setEnabled(selected);
      promptTemplateComboBox.setEnabled(selected);
      modelComboBox.setEnabled(!selected);
      modelSizeComboBox.setEnabled((!selected));
      huggingFaceModelComboBox.setEnabled((!selected));
    });

    serverProgressPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

    var llamaServerAgent = ApplicationManager.getApplication().getService(LlamaServerAgent.class);
    JButton button = new ServerActionButton(llamaServerAgent);
    button.addActionListener(e -> {
      if (llamaServerAgent.isServerRunning()) {
        button.setEnabled(true);
        updateServerProgressPanel(JBUI.Panels.simplePanel(0, 0)
            .addToLeft(new JBLabel("Stopping a server..."))
            .addToRight(new AsyncProcessIcon("sign_in_spinner")));

        llamaServerAgent.stopAgent();
      } else {
        var modelPath = useCustomModelCheckBox.isSelected() ?
            textFieldWithBrowseButton.getText() :
            "models/"
                + ((HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem()).getFileName();
        llamaServerAgent.startAgent(
            modelPath,
            () -> {
              button.setText("Stop Server");
              button.setIcon(Actions.Suspend);
              button.setEnabled(true);

              updateServerProgressPanel(new JBLabel(
                  "Server running",
                  Actions.Commit,
                  SwingConstants.TRAILING));
            },
            () -> {
              button.setText("Start Server");
              button.setIcon(Actions.Execute);
              button.setEnabled(true);

              updateServerProgressPanel(new JBLabel(
                  "Server terminated",
                  Actions.Cancel,
                  SwingConstants.TRAILING));
            },
            serverProgressPanel);

        button.setEnabled(false);
        updateServerProgressPanel(
            JBUI.Panels.simplePanel(0, 0)
                .addToLeft(new JBLabel("Starting a server..."))
                .addToRight(new AsyncProcessIcon("sign_in_spinner")));
      }
    });

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(createServerSettingsForm(huggingFaceComboBoxModel)))
        .addComponent(new TitledSeparator("Server Preferences"))
        .addComponent(withEmptyLeftBorder(FormBuilder.createFormBuilder()
            .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(button))
            .getPanel()))
        .addVerticalGap(4)
        .addComponent(withEmptyLeftBorder(serverProgressPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  public void setSelectedModel(HuggingFaceModel model) {
    huggingFaceComboBoxModel.setSelectedItem(model);
  }

  public HuggingFaceModel getSelectedModel() {
    return (HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem();
  }

  public void setModelDestinationPath(String modelPath) {
    textFieldWithBrowseButton.setText(modelPath);
  }

  public String getModelDestinationPath() {
    return textFieldWithBrowseButton.getText();
  }

  public void setServerPort(int serverPort) {
    portField.setNumber(serverPort);
  }

  public int getServerPort() {
    return portField.getNumber();
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private JPanel createServerSettingsForm(
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceModelComboBoxModel) {

    var progressLabel = new JBLabel("");
    downloadModelLinkWrapper.removeAll();
    downloadModelLinkWrapper.add(progressLabel);
    downloadModelLinkWrapper.repaint();
    downloadModelLinkWrapper.revalidate();

    var downloadModelLink = new AnActionLink(
        "Download model",
        new DownloadModelAction(
            () -> {
              downloadModelLinkWrapper.setVisible(false);
              modelExistsIcon.setVisible(true);
            },
            (error) -> {
              throw new RuntimeException(error);
            },
            (HuggingFaceModel) huggingFaceModelComboBoxModel.getSelectedItem(),
            progressLabel));
    downloadModelLinkWrapper.addToLeft(downloadModelLink);

    var helpText = ComponentPanelBuilder.createCommentComponent("Only .gguf files are supported",
        true);
    helpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(8));
    modelComboBoxWrapper.add(helpIcon);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addLabeledComponent("Model size:", modelSizeComboBox)
        .addLabeledComponent("Quantization:", huggingFaceModelComboBox)
        .addComponentToRightColumn(downloadModelLinkWrapper)
        .addVerticalGap(16)
        .addComponent(useCustomModelCheckBox)
        .addComponent(textFieldWithBrowseButton)
        .addComponent(helpText)
        .addVerticalGap(4)
        .addLabeledComponent("Prompt template:", promptTemplateComboBox)
        .getPanel();
  }

  private void updateModelHelpTooltip(HuggingFaceModel model) {
    helpIcon.setToolTipText(null);
    var llamaModel = LlamaModel.findByHuggingFaceModel(model);
    try {
      new HelpTooltip()
          .setTitle(llamaModel.getLabel())
          .setDescription("<html><p>" + model.getFileName() + "</p></html>")
          .setBrowserLink("Link to model", new URL(model.getFilePath()))
          .installOn(helpIcon);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private void updateServerProgressPanel(JComponent component) {
    serverProgressPanel.removeAll();
    serverProgressPanel.add(component);
    serverProgressPanel.repaint();
    serverProgressPanel.revalidate();
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

  class ServerActionButton extends JButton {

    public ServerActionButton(LlamaServerAgent llamaServerAgent) {
      updateComponent(llamaServerAgent.isServerRunning());
      addActionListener(e -> {
        if (llamaServerAgent.isServerRunning()) {
          setEnabled(true);
          updateServerProgressPanel(JBUI.Panels.simplePanel(0, 0)
              .addToLeft(new JBLabel("Stopping a server..."))
              .addToRight(new AsyncProcessIcon("sign_in_spinner")));
          llamaServerAgent.stopAgent();
        } else {
          var modelPath = useCustomModelCheckBox.isSelected() ?
              textFieldWithBrowseButton.getText() :
              "models/"
                  + ((HuggingFaceModel) huggingFaceComboBoxModel.getSelectedItem()).getFileName();
          llamaServerAgent.startAgent(
              modelPath,
              () -> {
                updateComponent(true);
                updateServerProgressPanel(new JBLabel(
                    "Server running",
                    Actions.Commit,
                    SwingConstants.TRAILING));
              },
              () -> {
                updateComponent(false);
                updateServerProgressPanel(new JBLabel(
                    "Server terminated",
                    Actions.Cancel,
                    SwingConstants.TRAILING));
              },
              serverProgressPanel);

          setEnabled(false);
          updateServerProgressPanel(
              JBUI.Panels.simplePanel(0, 0)
                  .addToLeft(new JBLabel("Starting a server..."))
                  .addToRight(new AsyncProcessIcon("sign_in_spinner")));
        }
      });
    }

    private void updateComponent(boolean serverRunning) {
      setText(serverRunning ? "Stop Server" : "Start Server");
      setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
      setEnabled(true);
    }
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
