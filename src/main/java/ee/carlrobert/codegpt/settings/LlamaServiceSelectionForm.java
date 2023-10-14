package ee.carlrobert.codegpt.settings;

import static java.lang.String.format;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.jetbrains.annotations.NotNull;

public class LlamaServiceSelectionForm extends JPanel {

  private final TextFieldWithBrowseButton textFieldWithBrowseButton;
  private final ComboBox<LlamaModel> modelComboBox;
  private final BorderLayoutPanel downloadModelLinkWrapper;
  private final JBLabel modelExistsIcon;

  public LlamaServiceSelectionForm() {
    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("gguf");
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    textFieldWithBrowseButton.addBrowseFolderListener(
        new TextBrowseFolderListener(fileChooserDescriptor));
    modelComboBox = new LlamaModelComboBox(
        new LlamaModel[]{
            LlamaModel.CODE_LLAMA_7B,
            LlamaModel.CODE_LLAMA_13B,
        },
        LlamaModel.CODE_LLAMA_7B);
    downloadModelLinkWrapper = JBUI.Panels.simplePanel();

    modelExistsIcon = new JBLabel(Actions.Commit);
    modelExistsIcon.setVisible(true);

    modelComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        var llamaModel = (LlamaModel) e.getItem();
        if (FileUtil.exists(
            CodeGPTPlugin.getLlamaModelsPath() + File.separator + llamaModel.getFileName())) {
          modelExistsIcon.setVisible(true);
          downloadModelLinkWrapper.removeAll();
          downloadModelLinkWrapper.repaint();
          downloadModelLinkWrapper.revalidate();
        } else {
          modelExistsIcon.setVisible(false);
          var progressLabel = new JBLabel("");

          downloadModelLinkWrapper.addToLeft(new AnActionLink(
              "Download model",
              new DownloadModelAction(() -> downloadModelLinkWrapper.setVisible(false),
                  progressLabel, downloadModelLinkWrapper)));
        }
      }
    });

    setLayout(new BorderLayout());
    add(FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Model Preferences"))
        .addComponent(withEmptyLeftBorder(createServerSettingsForm()))
        .getPanel());
  }

  private JPanel createServerSettingsForm() {
    var loadingSpinner = new AsyncProcessIcon("sign_in_spinner");
    loadingSpinner.setVisible(false);

    var progressLabel = new JBLabel("");

    var downloadModelLink = new AnActionLink(
        "Download model",
        new DownloadModelAction(
            () -> downloadModelLinkWrapper.setVisible(false),
            progressLabel,
            downloadModelLinkWrapper));
    downloadModelLinkWrapper.addToLeft(downloadModelLink);
    var startServerLink = new AnActionLink("Start Server", new StartServerAction(
        () -> {
          // TODO
        },
        () -> {
          // TODO
        }));

    var actionLinkWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 16));
    actionLinkWrapper.add(startServerLink);
    actionLinkWrapper.add(Box.createHorizontalStrut(8));
    actionLinkWrapper.add(loadingSpinner);

    var hostField = new JBTextField("http://localhost:8080/completions");
    hostField.setEnabled(false);
    var portField = new PortField(8080);
    portField.addChangeListener(changeEvent -> {
      var port = (int) ((PortField) changeEvent.getSource()).getValue();
      hostField.setText(format("http://localhost:%d/completions", port));
    });

    var modelComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    modelComboBoxWrapper.add(modelComboBox);
    modelComboBoxWrapper.add(Box.createHorizontalStrut(4));
    modelComboBoxWrapper.add(modelExistsIcon);

    return FormBuilder.createFormBuilder()
        .addLabeledComponent("Model:", modelComboBoxWrapper)
        .addComponentToRightColumn(downloadModelLinkWrapper)
        .addLabeledComponent("Model path:", textFieldWithBrowseButton)
        .addLabeledComponent("Host:", hostField)
        .addLabeledComponent("Port:", JBUI.Panels.simplePanel()
            .addToLeft(portField)
            .addToRight(startServerLink))
        .addComponent(JBUI.Panels.simplePanel().addToLeft(actionLinkWrapper))
        .getPanel();
  }

  class DownloadModelAction extends AnAction {

    private final Runnable onDownloaded;
    private final JBLabel progressLabel;
    private final BorderLayoutPanel downloadModelLinkWrapper;

    DownloadModelAction(
        Runnable onDownloaded,
        JBLabel progressLabel,
        BorderLayoutPanel downloadModelLinkWrapper) {
      this.onDownloaded = onDownloaded;
      this.progressLabel = progressLabel;
      this.downloadModelLinkWrapper = downloadModelLinkWrapper;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      Task.Backgroundable task = new Task.Backgroundable(null, "Downloading Model", true) {
        @Override
        public void run(@NotNull ProgressIndicator indicator) {
          var model = (LlamaModel) modelComboBox.getModel().getSelectedItem();

          URL url;
          try {
            url = new URL(model.getFilePath());
          } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
          }

          ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
          ScheduledFuture<?> future = null;

          try (
              var readableByteChannel = Channels.newChannel(url.openStream());
              var fileOutputStream = new FileOutputStream(
                  CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getFileName())) {

            downloadModelLinkWrapper.removeAll();
            downloadModelLinkWrapper.add(progressLabel);
            downloadModelLinkWrapper.repaint();
            downloadModelLinkWrapper.revalidate();

            indicator.setIndeterminate(false);
            indicator.setText(format("Downloading %s...", model.label));

            long fileSize = url.openConnection().getContentLengthLong();
            long[] bytesRead = {0}; // use an array to hold the value
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);

            long startTime = System.currentTimeMillis();
            future = executorService.scheduleAtFixedRate(() -> {
              long timeElapsed = System.currentTimeMillis() - startTime;
              double speed =
                  ((double) bytesRead[0] / timeElapsed) * 1000 / (1024 * 1024); // in MB/sec
              double percent = (double) bytesRead[0] / fileSize * 100; // in %
              double downloadedMB = (double) bytesRead[0] / (1024 * 1024); // in MB
              double totalMB = (double) fileSize / (1024 * 1024); // in MB

              progressLabel.setText(
                  String.format("%.2fMB of %.2fMB (%.2f%%), Speed: %.2fMB/sec", downloadedMB,
                      totalMB, percent, speed));
            }, 0, 250, TimeUnit.MILLISECONDS);

            while (readableByteChannel.read(buffer) != -1) {
              if (indicator.isCanceled()) {
                readableByteChannel.close();
                break;
              }
              buffer.flip();
              bytesRead[0] += fileOutputStream.getChannel()
                  .write(buffer); // update the array element
              buffer.clear();
              indicator.setFraction((double) bytesRead[0] / fileSize);
            }

            onDownloaded.run();
          } catch (Exception e) {
            throw new RuntimeException(e);
          } finally {
            if (future != null) {
              future.cancel(true);
            }
            executorService.shutdown();
          }
        }
      };

      ProgressManager.getInstance().run(task);
    }
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

  enum LlamaModel {
    CODE_LLAMA_7B(
        "TheBloke/CodeLlama-7B-Instruct (7.28 GB)",
        "codellama-7b-instruct.Q5_K_M.gguf",
        "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.Q5_K_M.gguf"),
    CODE_LLAMA_13B(
        "TheBloke/CodeLlama-13B-Instruct (9.23 GB)",
        "codellama-13b-instruct.Q5_K_M.gguf",
        "https://huggingface.co/TheBloke/CodeLlama-13B-Instruct-GGUF/resolve/main/codellama-13b-instruct.Q5_K_M.gguf");

    private final String label;
    private final String filePath;
    private final String fileName;

    LlamaModel(String label, String fileName, String filePath) {
      this.label = label;
      this.fileName = fileName;
      this.filePath = filePath;
    }

    public String getLabel() {
      return label;
    }

    public String getFileName() {
      return fileName;
    }

    public String getFilePath() {
      return filePath;
    }
  }

  static class LlamaModelComboBox extends ComboBox<LlamaModel> {

    public LlamaModelComboBox(LlamaModel[] options, LlamaModel selectedModel) {
      super(options);
      setSelectedItem(selectedModel);
      setRenderer(getBasicComboBoxRenderer());
    }

    private BasicComboBoxRenderer getBasicComboBoxRenderer() {
      return new BasicComboBoxRenderer() {
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
          super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

          if (value != null) {
            LlamaModel model = (LlamaModel) value;
            setText(model.getLabel());
          }
          return this;
        }
      };
    }
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  public void setModelDestinationPath(String modelPath) {
    textFieldWithBrowseButton.setText(modelPath);
  }

  public String getModelDestinationPath() {
    return textFieldWithBrowseButton.getText();
  }
}
