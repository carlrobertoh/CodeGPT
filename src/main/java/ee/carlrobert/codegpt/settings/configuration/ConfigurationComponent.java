package ee.carlrobert.codegpt.settings.configuration;

import static ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.DEFAULT_ACTIONS_ARRAY;
import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;

import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.keymap.impl.ui.EditKeymapsDialog;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.jetbrains.annotations.NotNull;

public class ConfigurationComponent {

  private final JPanel mainPanel;
  private final JBTable table;
  private final JBCheckBox checkForPluginUpdatesCheckBox;
  private final JBCheckBox checkForNewScreenshotsCheckBox;
  private final JBCheckBox openNewTabCheckBox;
  private final JBCheckBox methodNameGenerationCheckBox;
  private final JBCheckBox autoFormattingCheckBox;
  private final JTextArea systemPromptTextArea;
  private final JTextArea commitMessagePromptTextArea;
  private final IntegerField maxTokensField;
  private final JBTextField temperatureField;

  public ConfigurationComponent(Disposable parentDisposable, ConfigurationState configuration) {
    table = new JBTable(new DefaultTableModel(
        EditorActionsUtil.toArray(configuration.getTableData()),
        new String[]{
            CodeGPTBundle.get("configurationConfigurable.table.header.actionColumnLabel"),
            CodeGPTBundle.get("configurationConfigurable.table.header.promptColumnLabel")
        }));
    table.getColumnModel().getColumn(0).setPreferredWidth(60);
    table.getColumnModel().getColumn(1).setPreferredWidth(240);
    table.getEmptyText().setText(CodeGPTBundle.get("configurationConfigurable.table.emptyText"));
    var tablePanel = createTablePanel();
    tablePanel.setBorder(BorderFactory.createTitledBorder(
        CodeGPTBundle.get("configurationConfigurable.table.title")));

    temperatureField = new JBTextField(12);
    temperatureField.setText(String.valueOf(configuration.getTemperature()));

    var temperatureFieldValidator = createTemperatureInputValidator(parentDisposable,
        temperatureField);
    temperatureField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        temperatureFieldValidator.revalidate();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        temperatureFieldValidator.revalidate();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        temperatureFieldValidator.revalidate();
      }
    });

    maxTokensField = new IntegerField();
    maxTokensField.setColumns(12);
    maxTokensField.setValue(configuration.getMaxTokens());

    systemPromptTextArea = new JTextArea();
    if (configuration.getSystemPrompt().isBlank()) {
      // for backward compatibility
      systemPromptTextArea.setText(COMPLETION_SYSTEM_PROMPT);
    } else {
      systemPromptTextArea.setText(configuration.getSystemPrompt());
    }
    systemPromptTextArea.setLineWrap(true);
    systemPromptTextArea.setBorder(JBUI.Borders.empty(8, 4));
    systemPromptTextArea.setColumns(60);
    systemPromptTextArea.setRows(3);

    commitMessagePromptTextArea = new JBTextArea(configuration.getCommitMessagePrompt(),
        3, 60);
    commitMessagePromptTextArea.setLineWrap(true);
    commitMessagePromptTextArea.setBorder(JBUI.Borders.empty(8, 4));

    checkForPluginUpdatesCheckBox = new JBCheckBox(
        CodeGPTBundle.get("configurationConfigurable.checkForPluginUpdates.label"),
        configuration.isCheckForPluginUpdates());
    checkForNewScreenshotsCheckBox = new JBCheckBox(
        CodeGPTBundle.get("configurationConfigurable.checkForNewScreenshots.label"),
        configuration.isCheckForNewScreenshots());
    openNewTabCheckBox = new JBCheckBox(
        CodeGPTBundle.get("configurationConfigurable.openNewTabCheckBox.label"),
        configuration.isCreateNewChatOnEachAction());
    methodNameGenerationCheckBox = new JBCheckBox(
        CodeGPTBundle.get("configurationConfigurable.enableMethodNameGeneration.label"),
        configuration.isMethodNameGenerationEnabled());
    autoFormattingCheckBox = new JBCheckBox(
        CodeGPTBundle.get("configurationConfigurable.autoFormatting.label"),
        configuration.isAutoFormattingEnabled());

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(tablePanel)
        .addVerticalGap(4)
        .addComponent(checkForPluginUpdatesCheckBox)
        .addComponent(checkForNewScreenshotsCheckBox)
        .addComponent(openNewTabCheckBox)
        .addComponent(methodNameGenerationCheckBox)
        .addComponent(autoFormattingCheckBox)
        .addVerticalGap(4)
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("configurationConfigurable.section.assistant.title")))
        .addComponent(createAssistantConfigurationForm())
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("configurationConfigurable.section.commitMessage.title")))
        .addComponent(createCommitMessageConfigurationForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public ConfigurationState getCurrentFormState() {
    var state = new ConfigurationState();
    state.setTableData(getTableData());
    state.setMaxTokens(maxTokensField.getValue());
    state.setTemperature(Double.parseDouble(temperatureField.getText()));
    state.setSystemPrompt(systemPromptTextArea.getText());
    state.setCommitMessagePrompt(commitMessagePromptTextArea.getText());
    state.setCheckForPluginUpdates(checkForPluginUpdatesCheckBox.isSelected());
    state.setCheckForNewScreenshots(checkForNewScreenshotsCheckBox.isSelected());
    state.setCreateNewChatOnEachAction(openNewTabCheckBox.isSelected());
    state.setMethodNameGenerationEnabled(methodNameGenerationCheckBox.isSelected());
    state.setAutoFormattingEnabled(autoFormattingCheckBox.isSelected());
    state.setCodeCompletionsEnabled(
        ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled());
    return state;
  }

  public void resetForm() {
    var configuration = ConfigurationSettings.getCurrentState();
    setTableData(configuration.getTableData());
    maxTokensField.setValue(configuration.getMaxTokens());
    temperatureField.setText(String.valueOf(configuration.getTemperature()));
    systemPromptTextArea.setText(configuration.getSystemPrompt());
    commitMessagePromptTextArea.setText(configuration.getCommitMessagePrompt());
    checkForPluginUpdatesCheckBox.setSelected(configuration.isCheckForPluginUpdates());
    checkForNewScreenshotsCheckBox.setSelected(configuration.isCheckForNewScreenshots());
    openNewTabCheckBox.setSelected(configuration.isCreateNewChatOnEachAction());
    methodNameGenerationCheckBox.setSelected(configuration.isMethodNameGenerationEnabled());
    autoFormattingCheckBox.setSelected(configuration.isAutoFormattingEnabled());
  }

  private Map<String, String> getTableData() {
    var model = getModel();
    Map<String, String> data = new LinkedHashMap<>();
    for (int count = 0; count < model.getRowCount(); count++) {
      data.put(
          model.getValueAt(count, 0).toString(),
          model.getValueAt(count, 1).toString());
    }
    return data;
  }

  private JPanel createTablePanel() {
    return ToolbarDecorator.createDecorator(table)
        .setPreferredSize(new Dimension(table.getPreferredSize().width, 140))
        .setAddAction(anActionButton -> getModel().addRow(new Object[]{"", ""}))
        .setRemoveAction(anActionButton -> getModel().removeRow(table.getSelectedRow()))
        .disableUpAction()
        .disableDownAction()
        .addExtraAction(new RevertToDefaultsActionButton())
        .addExtraAction(new KeymapActionButton())
        .createPanel();
  }

  // Formatted keys are not referenced in the messages bundle file
  private void addAssistantFormLabeledComponent(
      FormBuilder formBuilder,
      String labelKey,
      String commentKey,
      JComponent component) {
    formBuilder.addLabeledComponent(
        new JBLabel(CodeGPTBundle.get(labelKey))
            .withBorder(JBUI.Borders.emptyLeft(2)),
        UI.PanelFactory.panel(component)
            .resizeX(false)
            .withComment(CodeGPTBundle.get(commentKey))
            .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
            .createPanel(),
        true
    );
  }

  private JPanel createAssistantConfigurationForm() {
    var formBuilder = FormBuilder.createFormBuilder();
    addAssistantFormLabeledComponent(
        formBuilder,
        "configurationConfigurable.section.assistant.systemPromptField.label",
        "configurationConfigurable.section.assistant.systemPromptField.comment",
        JBUI.Panels
            .simplePanel(systemPromptTextArea)
            .withBorder(JBUI.Borders.customLine(
                JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground())));
    formBuilder.addVerticalGap(8);
    addAssistantFormLabeledComponent(
        formBuilder,
        "configurationConfigurable.section.assistant.temperatureField.label",
        "configurationConfigurable.section.assistant.temperatureField.comment",
        temperatureField);
    addAssistantFormLabeledComponent(
        formBuilder,
        "configurationConfigurable.section.assistant.maxTokensField.label",
        "configurationConfigurable.section.assistant.maxTokensField.comment",
        maxTokensField);

    var form = formBuilder.getPanel();
    form.setBorder(JBUI.Borders.emptyLeft(16));
    return form;
  }

  private JPanel createCommitMessageConfigurationForm() {
    var formBuilder = FormBuilder.createFormBuilder();
    addAssistantFormLabeledComponent(
        formBuilder,
        "configurationConfigurable.section.commitMessage.systemPromptField.label",
        "configurationConfigurable.section.commitMessage.systemPromptField.comment",
        JBUI.Panels
            .simplePanel(commitMessagePromptTextArea)
            .withBorder(JBUI.Borders.customLine(
                JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground())));
    formBuilder.addVerticalGap(8);

    var form = formBuilder.getPanel();
    form.setBorder(JBUI.Borders.emptyLeft(16));
    return form;
  }

  private ComponentValidator createTemperatureInputValidator(
      Disposable parentDisposable,
      JBTextField component) {
    var validator = new ComponentValidator(parentDisposable)
        .withValidator(() -> {
          var valueText = component.getText();
          try {
            var value = Double.parseDouble(valueText);
            if (value > 1.0 || value < 0.0) {
              return new ValidationInfo(
                  CodeGPTBundle.get("validation.error.mustBeBetweenZeroAndOne"),
                  component);
            }
          } catch (NumberFormatException e) {
            return new ValidationInfo(
                CodeGPTBundle.get("validation.error.mustBeNumber"),
                component);
          }

          return null;
        })
        .andStartOnFocusLost()
        .installOn(component);
    validator.enableValidation();
    return validator;
  }

  private DefaultTableModel getModel() {
    return (DefaultTableModel) table.getModel();
  }

  public void setTableData(Map<String, String> tableData) {
    var model = getModel();
    model.setNumRows(0);
    tableData.forEach((action, prompt) -> model.addRow(new Object[]{action, prompt}));
  }

  class RevertToDefaultsActionButton extends AnActionButton {

    RevertToDefaultsActionButton() {
      super(
          CodeGPTBundle.get("configurationConfigurable.table.action.revertToDefaults.text"),
          AllIcons.Actions.Rollback);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      var model = getModel();
      model.setRowCount(0);
      Arrays.stream(DEFAULT_ACTIONS_ARRAY).forEach(model::addRow);
      EditorActionsUtil.refreshActions();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
      return ActionUpdateThread.EDT;
    }
  }

  class KeymapActionButton extends AnActionButton {

    KeymapActionButton() {
      super(
          CodeGPTBundle.get("configurationConfigurable.table.action.addKeymap.text"),
          Nodes.KeymapEditor);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      var actionId = "codegpt.AskChatgpt";
      var selectedRow = table.getSelectedRow();
      if (selectedRow != -1) {
        var label = getModel()
            .getDataVector()
            .get(selectedRow)
            .get(0);
        if (label != null && !label.toString().isEmpty()) {
          actionId = EditorActionsUtil.convertToId(label.toString());
        }
      }
      new EditKeymapsDialog(e.getProject(), actionId, false).show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
      return ActionUpdateThread.EDT;
    }
  }
}
