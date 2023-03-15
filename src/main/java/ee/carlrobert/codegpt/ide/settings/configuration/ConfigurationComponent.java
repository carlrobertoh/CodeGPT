package ee.carlrobert.codegpt.ide.settings.configuration;

import static ee.carlrobert.codegpt.ide.action.ActionsUtil.DEFAULT_ACTIONS_ARRAY;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.ide.action.ActionsUtil;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jetbrains.annotations.NotNull;

public class ConfigurationComponent {

  private final JPanel mainPanel;
  private final JBTable table;

  public ConfigurationComponent(ConfigurationState configuration) {
    table = new JBTable(new DefaultTableModel(
        ActionsUtil.toArray(configuration.tableData),
        new String[] {"Action", "Prompt"}));
    table.getColumnModel().getColumn(0).setPreferredWidth(140);
    table.getColumnModel().getColumn(0).setMaxWidth(140);
    table.getEmptyText().setText("No actions configured");

    var tablePanel = createTablePanel();
    tablePanel.setBorder(BorderFactory.createTitledBorder("Action prompts"));

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Configuration Preference"))
        .addVerticalGap(8)
        .addComponent(tablePanel)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public Map<String, String> getTableData() {
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
        .setPreferredSize(new Dimension(table.getPreferredSize().width, 160))
        .setAddAction(anActionButton -> getModel().addRow(new Object[] {"", ""}))
        .setRemoveAction(anActionButton -> getModel().removeRow(table.getSelectedRow()))
        .disableUpAction()
        .disableDownAction()
        .addExtraAction(new RevertToDefaultsActionButton())
        .createPanel();
  }

  private DefaultTableModel getModel() {
    return (DefaultTableModel) table.getModel();
  }

  public void setTableData(Map<String, String> tableData) {
    var model = getModel();
    model.setNumRows(0);
    tableData.forEach((action, prompt) -> model.addRow(new Object[] {action, prompt}));
  }

  class RevertToDefaultsActionButton extends AnActionButton {

    RevertToDefaultsActionButton() {
      super("Revert to Defaults", AllIcons.Actions.Rollback);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      var model = getModel();
      model.setNumRows(0);
      Arrays.stream(DEFAULT_ACTIONS_ARRAY).forEach(model::addRow);
    }
  }
}
