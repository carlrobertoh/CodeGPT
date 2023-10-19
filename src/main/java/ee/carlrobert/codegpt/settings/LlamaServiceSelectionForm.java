package ee.carlrobert.codegpt.settings;

import static java.lang.String.format;

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
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.io.File;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

public class LlamaServiceSelectionForm extends JPanel {

  private final TextFieldWithBrowseButton textFieldWithBrowseButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final BorderLayoutPanel downloadModelLinkWrapper;
  private final JBLabel modelExistsIcon;
  private final JBTextField hostField;
  private final PortField portField;

  public LlamaServiceSelectionForm() {
    var llamaSettings = LlamaSettingsState.getInstance();
    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    textFieldWithBrowseButton.addBrowseFolderListener(
        new TextBrowseFolderListener(fileChooserDescriptor));

    modelComboBox = new ComboBox<>(new EnumComboBoxModel<>(LlamaModel.class));
    modelComboBox.setSelectedItem(LlamaModel.CODE_LLAMA_7B);
    downloadModelLinkWrapper = JBUI.Panels.simplePanel().withBorder(JBUI.Borders.emptyLeft(2));
    modelExistsIcon = new JBLabel(Actions.Commit);
    modelExistsIcon.setVisible(isModelExists(llamaSettings.getLlamaModel()));
    modelComboBox.addItemListener(e -> {
      var modelExists = isModelExists((LlamaModel) e.getItem());
      modelExistsIcon.setVisible(modelExists);
      downloadModelLinkWrapper.setVisible(!modelExists);
    });
    hostField = new JBTextField(getHost(llamaSettings.getServerPort()));
    hostField.setEnabled(false);
    portField = new PortField(llamaSettings.getServerPort());
    portField.addChangeListener(changeEvent -> {
      var port = (int) ((PortField) changeEvent.getSource()).getValue();
      hostField.setText(getHost(port));
    });

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(createServerSettingsForm()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel());
  }

  public void setSelectedModel(LlamaModel model) {
    modelComboBox.setSelectedItem(model);
  }

  public LlamaModel getSelectedModel() {
    return (LlamaModel) modelComboBox.getSelectedItem();
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

    private final Runnable onStart;
    private final Runnable onSuccess;

    StartServerAction(Runnable onStart, Runnable onSuccess) {
      this.onStart = onStart;
      this.onSuccess = onSuccess;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      onStart.run();
      new LlamaServerAgent(textFieldWithBrowseButton.getText()).startAgent(onSuccess);
    }
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  private String getHost(int port) {
    return format("http://localhost:%d/completions", port);
  }

  private boolean isModelExists(LlamaModel model) {
    return FileUtil.exists(
        CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName());
  }

  private JPanel createServerSettingsForm() {
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
            (LlamaModel) modelComboBox.getSelectedItem(),
            downloadModelLinkWrapper));
    downloadModelLinkWrapper.addToLeft(downloadModelLink);

    var startServerLinkWrapper = JBUI.Panels.simplePanel();
    var startServerLink = new AnActionLink("Start Server", new StartServerAction(
        () -> {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(new JBLabel("Starting a server"));
          startServerLinkWrapper.add(new AsyncProcessIcon("sign_in_spinner"));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();
        },
        () -> {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(
              new JBLabel("Server running", Actions.Commit, SwingConstants.RIGHT));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();
        }));
    startServerLinkWrapper.add(startServerLink);

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    var helpText = ComponentPanelBuilder.createCommentComponent("Only .gguf files are supported", true);
    helpText.setBorder(JBUI.Borders.empty(0, 4));

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addComponentToRightColumn(downloadModelLinkWrapper)
        .addLabeledComponent("Model path:", textFieldWithBrowseButton)
        .addComponentToRightColumn(helpText)
        .addLabeledComponent("Host:", hostField)
        .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
            .addToLeft(portField)
            .addToRight(startServerLinkWrapper))
        .getPanel();
  }
}
