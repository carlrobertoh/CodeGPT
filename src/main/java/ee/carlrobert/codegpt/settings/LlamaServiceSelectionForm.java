package ee.carlrobert.codegpt.settings;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

public class LlamaServiceSelectionForm extends JPanel {

  private final TextFieldWithBrowseButton textFieldWithBrowseButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<Integer> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final BorderLayoutPanel downloadModelLinkWrapper;
  private final JBLabel modelExistsIcon;
  private final JBTextField hostField;
  private final PortField portField;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;
  private final JBCheckBox overrideHostCheckBox;

  public LlamaServiceSelectionForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    var llm = llamaSettings.getHuggingFaceModel();
    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    textFieldWithBrowseButton = new TextFieldWithBrowseButton();
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
      var modelExists = isModelExists((HuggingFaceModel) e.getItem());
      modelExistsIcon.setVisible(modelExists);
      downloadModelLinkWrapper.setVisible(!modelExists);
    });

    var modelSizeComboBoxModel = new DefaultComboBoxModel<Integer>();
    var initialModelSizes = llamaModel.getHuggingFaceModels().stream()
        .filter(distinctByKey(HuggingFaceModel::getParameterSize))
        .map(HuggingFaceModel::getParameterSize)
        .collect(toList());
    modelSizeComboBoxModel.addAll(initialModelSizes);

    modelComboBox = new ComboBox<>(new EnumComboBoxModel<>(LlamaModel.class));
    modelComboBox.setSelectedItem(llamaModel);
    modelComboBox.addItemListener(e -> {
      // TODO
      var models = ((LlamaModel) e.getItem()).getHuggingFaceModels().stream()
          .filter(
              model -> modelSizeComboBoxModel.getSelectedItem().equals(model.getParameterSize()))
          .collect(toList());
      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(models);
      huggingFaceComboBoxModel.setSelectedItem(models.get(0));
    });

    modelSizeComboBox = new ComboBox<>(modelSizeComboBoxModel);
    modelSizeComboBox.setSelectedItem(initialModelSizes.get(0));
    modelSizeComboBox.addItemListener(size -> {
      var selectedModel = (LlamaModel) modelComboBox.getSelectedItem();
      var models = requireNonNull(selectedModel).getHuggingFaceModels().stream()
          .filter(
              model -> modelSizeComboBoxModel.getSelectedItem().equals(model.getParameterSize()))
          .collect(toList());
      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(models);
      huggingFaceComboBoxModel.setSelectedItem(models.get(0));
    });

    hostField = new JBTextField(llamaSettings.getHost());
    hostField.setEnabled(llamaSettings.isOverrideHost());
    portField = new PortField(llamaSettings.getServerPort());
    portField.setEnabled(!llamaSettings.isOverrideHost());
    portField.addChangeListener(changeEvent -> {
      var port = (int) ((PortField) changeEvent.getSource()).getValue();
      hostField.setText(getHost(port));
    });
    overrideHostCheckBox = new JBCheckBox("Override host", llamaSettings.isOverrideHost());
    overrideHostCheckBox.addChangeListener(e -> {
      var isSelected = ((JBCheckBox) e.getSource()).isSelected();
      hostField.setEnabled(isSelected);
      portField.setEnabled(!isSelected);
    });

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(createServerSettingsForm(huggingFaceComboBoxModel)))
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

  public boolean isOverrideLamaServerHost() {
    return overrideHostCheckBox.isSelected();
  }

  public String getLlamaServerHost() {
    return hostField.getText();
  }

  class StartServerAction extends AnAction {

    private final JPanel startServerLinkWrapper;
    private final Runnable onStart;
    private final Runnable onSuccess;

    StartServerAction(
        JPanel startServerLinkWrapper,
        Runnable onStart,
        Runnable onSuccess) {
      this.startServerLinkWrapper = startServerLinkWrapper;
      this.onStart = onStart;
      this.onSuccess = onSuccess;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      new LlamaServerAgent(textFieldWithBrowseButton.getText())
          .startAgent("", onSuccess, startServerLinkWrapper);
      onStart.run();
    }
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  private String getHost(int port) {
    return format("http://localhost:%d/completion", port);
  }

  private boolean isModelExists(HuggingFaceModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private JPanel createServerSettingsForm(
      DefaultComboBoxModel<HuggingFaceModel> huggingFaceModelComboBoxModel) {

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
            huggingFaceModelComboBoxModel,
            downloadModelLinkWrapper));
    downloadModelLinkWrapper.addToLeft(downloadModelLink);

    var startServerLinkWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
    var startServerLink = new AnActionLink("Start Server", new StartServerAction(
        startServerLinkWrapper,
        () -> {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(JBUI.Panels.simplePanel(4, 0)
              .addToLeft(new JBLabel("Starting a server..."))
              .addToRight(new AsyncProcessIcon("sign_in_spinner")));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();
        },
        () -> {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(
              new JBLabel("Server running", Actions.Commit, SwingConstants.TRAILING));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();
        }));
    startServerLinkWrapper.add(startServerLink);

    var helpText = ComponentPanelBuilder.createCommentComponent("Only .gguf files are supported",
        true);
    helpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addLabeledComponent("Model size (B):", modelSizeComboBox)
        .addLabeledComponent("Quantization:", huggingFaceModelComboBox)
        .addComponentToRightColumn(downloadModelLinkWrapper)
        .addLabeledComponent("Model path:", textFieldWithBrowseButton)
        .addComponentToRightColumn(helpText)
        .addLabeledComponent("Host:", hostField)
        .addComponentToRightColumn(overrideHostCheckBox)
        .addVerticalGap(4)
        .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
            .addToLeft(portField)
            .addToRight(startServerLinkWrapper))
        .getPanel();
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}
