package ee.carlrobert.codegpt.settings;

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
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
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
import org.jetbrains.annotations.NotNull;

public class LlamaServiceSelectionForm extends JPanel {

  private final TextFieldWithBrowseButton textFieldWithBrowseButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final ComboBox<ModelSize> modelSizeComboBox;
  private final ComboBox<HuggingFaceModel> huggingFaceModelComboBox;
  private final BorderLayoutPanel downloadModelLinkWrapper;
  private final JBLabel modelExistsIcon;
  private final PortField portField;
  private final DefaultComboBoxModel<HuggingFaceModel> huggingFaceComboBoxModel;

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

    var modelSizeComboBoxModel = new DefaultComboBoxModel<ModelSize>();
    var initialModelSizes = llamaModel.getHuggingFaceModels().stream()
        .filter(distinctByKey(HuggingFaceModel::getParameterSize))
        .map(model -> new ModelSize(model.getParameterSize()))
        .collect(toList());
    modelSizeComboBoxModel.addAll(initialModelSizes);

    modelComboBox = new ComboBox<>(new EnumComboBoxModel<>(LlamaModel.class));
    modelComboBox.setSelectedItem(llamaModel);
    modelComboBox.addItemListener(e -> {
      var models = ((LlamaModel) e.getItem()).getHuggingFaceModels().stream()
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
      var selectedModel = (LlamaModel) modelComboBox.getSelectedItem();
      var models = requireNonNull(selectedModel).getHuggingFaceModels().stream()
          .filter(model -> {
            var size = ((ModelSize) modelSizeComboBoxModel.getSelectedItem()).getSize();
            return size == model.getParameterSize();
          })
          .collect(toList());
      huggingFaceComboBoxModel.removeAllElements();
      huggingFaceComboBoxModel.addAll(models);
      huggingFaceComboBoxModel.setSelectedItem(models.get(0));
    });

    portField = new PortField(llamaSettings.getServerPort());

    var startServerLinkWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    var startServerLink = new AnActionLink("Start Server", new StartServerAction(
        startServerLinkWrapper,
        () -> {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(JBUI.Panels.simplePanel(0, 0)
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

    var portAvailable = isPortAvailable(llamaSettings.getServerPort());
    JButton button;
    if (portAvailable) {
      button = new JButton("Stop Server", Actions.Pause);
    } else {
      button = new JButton("Start Server", Actions.Resume);
    }
    button.addActionListener(e -> {
      new LlamaServerAgent(textFieldWithBrowseButton.getText())
          .startAgent("", () -> {
            startServerLinkWrapper.removeAll();
            startServerLinkWrapper.add(
                new JBLabel("Server running", Actions.Commit, SwingConstants.TRAILING));
            startServerLinkWrapper.repaint();
            startServerLinkWrapper.revalidate();
          }, startServerLinkWrapper);
      startServerLinkWrapper.removeAll();
      startServerLinkWrapper.add(JBUI.Panels.simplePanel(0, 0)
          .addToLeft(new JBLabel("Starting a server..."))
          .addToRight(new AsyncProcessIcon("sign_in_spinner")));
      startServerLinkWrapper.repaint();
      startServerLinkWrapper.revalidate();
    });

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(createServerSettingsForm(huggingFaceComboBoxModel)))
        .addComponent(new TitledSeparator("Server Preferences"))
        .addComponent(withEmptyLeftBorder(FormBuilder.createFormBuilder()
            .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
                .addToLeft(portField)
                .addToRight(button))
            .getPanel()))
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

  class StartServerAction extends AnAction {

    private final JPanel startServerLinkWrapper;
    private final Runnable onStart;
    private final Runnable onSuccess;

    StartServerAction(
        JPanel startServerLinkWrapper,
        Runnable onStart,
        Runnable onSuccess) {
      super("Start Server", "Start llama.cpp server", Actions.Resume);
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

    var helpText = ComponentPanelBuilder.createCommentComponent("Only .gguf files are supported",
        true);
    helpText.setBorder(JBUI.Borders.empty(0, 4));

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    var useCustomModelCheckBox = new JBCheckBox("Use custom model");
    useCustomModelCheckBox.addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      textFieldWithBrowseButton.setEnabled(selected);
      modelComboBox.setEnabled(!selected);
      modelSizeComboBox.setEnabled((!selected));
      huggingFaceModelComboBox.setEnabled((!selected));
    });

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addLabeledComponent("Model size:", modelSizeComboBox)
        .addLabeledComponent("Quantization:", huggingFaceModelComboBox)
        .addComponentToRightColumn(downloadModelLinkWrapper)
        .addVerticalGap(16)
        .addLabeledComponent("Model path:", textFieldWithBrowseButton)
        .addComponentToRightColumn(helpText)
        .addComponentToRightColumn(useCustomModelCheckBox)
        .getPanel();
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
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

  public static boolean isPortAvailable(int port) {
    try (var serverSocket = new ServerSocket(port); var datagramSocket = new DatagramSocket(port)) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
