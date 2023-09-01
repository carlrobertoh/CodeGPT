
package ee.carlrobert.codegpt.settings;

import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.settings.state.CustomSettingsState;
import ee.carlrobert.codegpt.util.ApplicationUtils;
import ee.carlrobert.codegpt.util.EditorUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

public class CustomServiceSelectionForm {

  private final JPanel rootPanel;
  private final boolean enabled;
  private final URLTextArea urlTextArea;
  private final ComboBox<CustomServicePreset> servicePresetComboBox;
  private final JBTable headersTable;
  private final JBTable queryParamsTable;
  private final EditorEx requestBodyEditor;
  private final EditorEx responseBodyEditor;
  private final JBCheckBox useStringTypeResponseCheckBox;

  public CustomServiceSelectionForm() {
    this(true);
  }

  public CustomServiceSelectionForm(boolean enabled) {
    this.enabled = enabled;
    urlTextArea = new URLTextArea(CustomSettingsState.getInstance().getUrl(), enabled);
    servicePresetComboBox = createCustomServicePresetComboBox();
    headersTable = new JBTable(new DefaultTableModel(
        EditorActionsUtil.toArray(CustomSettingsState.getInstance().getHeaders()),
        new String[] {"Key", "Value"}));
    queryParamsTable = new JBTable(new DefaultTableModel(
        EditorActionsUtil.toArray(CustomSettingsState.getInstance().getQueryParams()),
        new String[] {"Key", "Value"}));
    requestBodyEditor = createEditor("JSON", CustomSettingsState.getInstance().getBodyJson(), "request_body");
    responseBodyEditor = createEditor("JSON", CustomSettingsState.getInstance().getResponseJson(), "response_body");
    useStringTypeResponseCheckBox = new JBCheckBox("Use string type response", true);
    useStringTypeResponseCheckBox.addItemListener(e -> {
      responseBodyEditor.getContentComponent().setEnabled(e.getStateChange() != ItemEvent.SELECTED);
      responseBodyEditor.setViewer(e.getStateChange() == ItemEvent.SELECTED);
    });
    rootPanel = createForm();
  }

  public JPanel getForm() {
    return rootPanel;
  }

  private JPanel createForm() {
    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Request Configuration"))
        .addComponent(withEmptyLeftBorder(UI.PanelFactory.panel(servicePresetComboBox)
            .withLabel("Preset:")
            .resizeX(false)
            .createPanel()))
        .addComponent(withEmptyLeftBorder(JBUI.Panels.simplePanel(urlTextArea)))
        .addComponent(withEmptyLeftBorder(createTabbedPane()))
        .addComponent(new TitledSeparator("Response Configuration"))
        .addComponent(withEmptyLeftBorder(JBUI.Panels.simplePanel(responseBodyEditor.getComponent())))
        .addComponent(withEmptyLeftBorder(JBUI.Panels.simplePanel()
            .addToLeft(useStringTypeResponseCheckBox)
            .addToRight(createTestConnectionButton())))
        .getPanel();
  }

  private JButton createTestConnectionButton() {
    var button = new JButton("Test Connection");
    button.setEnabled(enabled);
    button.addActionListener(e -> {
      var client = CompletionClientProvider.getClientBuilder()
          .buildHttpClient();
      try (Response response = client.newCall(new Request.Builder().url("https://google.com").build()).execute()) {
        button.setIcon(response.isSuccessful() ? AllIcons.Actions.Commit : AllIcons.Actions.QuickfixBulb);
      } catch (IOException ex) {
        button.setIcon(AllIcons.Actions.QuickfixBulb);
      }
    });
    return button;
  }

  private ComboBox<CustomServicePreset> createCustomServicePresetComboBox() {
    var presetComboBoxModel = new DefaultComboBoxModel<CustomServicePreset>();
    presetComboBoxModel.addAll(List.of(
        CustomServicePreset.ANTHROPIC,
        CustomServicePreset.HUGGINGFACE,
        CustomServicePreset.LLAMA_2,
        CustomServicePreset.OPENAI,
        CustomServicePreset.AZURE_OPENAI));
    presetComboBoxModel.setSelectedItem(CustomServicePreset.ANTHROPIC);
    var presetComboBox = new ComboBox<>(presetComboBoxModel);
    presetComboBox.addItemListener(e -> {
      var presetDetails = ((CustomServicePreset) e.getItem()).getPresetDetails();
      urlTextArea.setText(presetDetails.getUrl());
      headersTable.setModel(new DefaultTableModel(
          EditorActionsUtil.toArray(presetDetails.getHeaders()),
          new String[] {"Key", "Value"}));
      queryParamsTable.setModel(new DefaultTableModel(
          EditorActionsUtil.toArray(presetDetails.getQueryParams()),
          new String[] {"Key", "Value"}));

      var application = ApplicationManager.getApplication();
      application.invokeLater(() ->
          application.runWriteAction(() -> WriteCommandAction.runWriteCommandAction(ApplicationUtils.findCurrentProject(), () -> {
            requestBodyEditor.getDocument().setText(presetDetails.getBodyJson());
            responseBodyEditor.getDocument().setText(presetDetails.getResponseJson());
          })));
    });
    presetComboBox.setEnabled(enabled);
    return presetComboBox;
  }

  private JTabbedPane createTabbedPane() {
    var tabbedPane = new JBTabbedPane();
    tabbedPane.setEnabled(enabled);
    tabbedPane.insertTab("Headers", null, createHeaderTabContent(), null, 0);
    tabbedPane.insertTab("Params", null, createParamsTabContent(), null, 1);
    tabbedPane.insertTab("Body", null, createBodyTabContent(), null, 2);
    tabbedPane.insertTab("Settings", AllIcons.Actions.InlayGear, createSettingsTabContent(), null, 3);
    tabbedPane.setSelectedIndex(2);
    return tabbedPane;
  }

  private JPanel createHeaderTabContent() {
    headersTable.setAdditionalRowsCount(1);
    headersTable.getEmptyText().setText("No headers configured");
    return JBUI.Panels
        .simplePanel(createTablePanel(headersTable, "Headers"))
        .withBorder(JBUI.Borders.emptyTop(4));
  }

  private JPanel createParamsTabContent() {
    queryParamsTable.setAdditionalRowsCount(1);
    queryParamsTable.getEmptyText().setText("No query params configured");
    return JBUI.Panels
        .simplePanel(createTablePanel(queryParamsTable, "Query Params"))
        .withBorder(JBUI.Borders.emptyTop(4));
  }

  private JPanel createBodyTabContent() {
    var templateComboBoxModel = new DefaultComboBoxModel<String>();
    templateComboBoxModel.addAll(List.of("SYSTEM_PROMPT", "PROMPT", "PROMPT_WITH_HISTORY"));
    templateComboBoxModel.setSelectedItem("PROMPT");
    var templateComboBox = new ComboBox<>(templateComboBoxModel);
    templateComboBox.setEnabled(enabled);

    var flowLayout = new FlowLayout(FlowLayout.RIGHT);
    flowLayout.setHgap(8);
    var wrapper = new JPanel(flowLayout);
    wrapper.add(templateComboBox);
    wrapper.add(createHelpTooltipLabel("PROMPT", "<p><strong>Type: </strong>String</p>"));

    templateComboBox.addItemListener(e -> {
      wrapper.removeAll();
      wrapper.add(templateComboBox);
      wrapper.add(createHelpTooltipLabel((String) e.getItem(), "<p><strong>Type: </strong>String</p>"));
      wrapper.repaint();
      wrapper.revalidate();
    });

    return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(wrapper)
            .withLabel("Placeholders:")
            .resizeX(false)
            .createPanel())
        .addComponent(JBUI.Panels.simplePanel(requestBodyEditor.getComponent()))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private Component createSettingsTabContent() {
    var useResponseStreamingCheckBox = new JBCheckBox("Use response streaming", true);
    useResponseStreamingCheckBox.setEnabled(false);

    // https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/CustomTrust.java
    var uploadCertificateButton = new JButton("Upload Certificate");
    uploadCertificateButton.addActionListener(e -> {
      var descriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
        @Override
        public boolean isFileSelectable(@Nullable VirtualFile file) {
          return file != null && !file.isDirectory() && List.of("pem", "der", "crt", "cer").contains(file.getExtension());
        }
      };

      var fileChooser = FileChooserFactory.getInstance().createFileChooser(descriptor, null, uploadCertificateButton);
      var certificates = fileChooser.choose(null);
      try {
        // TODO
        var content = new String(certificates[0].contentsToByteArray(), certificates[0].getCharset());
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });

    return FormBuilder.createFormBuilder()
        .addComponent(useResponseStreamingCheckBox)
        .addComponent(new JBCheckBox("Enable SSL certificate verification"))
        .addComponent(uploadCertificateButton)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JPanel createTablePanel(JBTable table, String title) {
    var tablePanel = ToolbarDecorator.createDecorator(table)
        .setPreferredSize(new Dimension(table.getPreferredSize().width, 20))
        .setAddAction(anActionButton -> getModel(table).addRow(new Object[] {"", ""}))
        .setRemoveAction(anActionButton -> getModel(table).removeRow(table.getSelectedRow()))
        .disableUpAction()
        .disableDownAction()
        .createPanel();
    tablePanel.setBorder(BorderFactory.createTitledBorder(title));
    return tablePanel;
  }

  private DefaultTableModel getModel(JBTable table) {
    return (DefaultTableModel) table.getModel();
  }

  private Document createDocument(LightVirtualFile virtualFile) {
    var document = FileDocumentManager.getInstance().getDocument(virtualFile);
    if (document == null) {
      document = EditorFactory.getInstance().createDocument(virtualFile.getContent());
    }
    return document;
  }

  private EditorEx createEditor(String title, String json, String tempFileName) {
    var fileName = tempFileName + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".json";
    var lightVirtualFile = new LightVirtualFile(String.format("%s/%s", PathManager.getTempPath(), fileName), json);
    var document = createDocument(lightVirtualFile);

    var editor = (EditorEx) EditorFactory.getInstance().createEditor(document,
        ApplicationUtils.findCurrentProject(),
        lightVirtualFile,
        !enabled,
        enabled ? EditorKind.UNTYPED : EditorKind.PREVIEW);
    editor.getSettings().setAdditionalLinesCount(1);
    editor.getScrollPane().setPreferredSize(editor.getScrollPane().getPreferredSize());
    editor.getComponent().setBorder(BorderFactory.createTitledBorder(title));
    EditorUtils.disableHighlighting(Objects.requireNonNull(ApplicationUtils.findCurrentProject()), document);
    return editor;
  }

  private JLabel createHelpTooltipLabel(String title, String description) {
    var tooltipLabel = new JLabel();
    tooltipLabel.setIcon(AllIcons.General.ContextHelp);
    new HelpTooltip().setTitle(title).setDescription(description).installOn(tooltipLabel);
    return tooltipLabel;
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  public String getUrl() {
    return urlTextArea.getText();
  }

  public String getHttpMethod() {
    return urlTextArea.getHttpMethod();
  }

  public Map<String, String> getHeaders() {
    return ((DefaultTableModel) headersTable.getModel()).getDataVector()
        .stream()
        .collect(Collectors.toMap(
            header -> (String) header.get(0),
            header -> (String) header.get(1)
        ));
    /*

    var headers = ((DefaultTableModel) headersTable.getModel()).getDataVector();

    var map = new HashMap<String, String>();
    for (int i = 0; i < headers.size(); i++) {
      var header = headers.get(i);
      map.put((String) header.get(0), (String) header.get(1));
    }
    return map;*/
  }

  public Object getQueryParams() {
    return null;
  }

  public Object getBodyJson() {
    return null;
  }

  public Object getResponseJson() {
    return null;
  }
}
