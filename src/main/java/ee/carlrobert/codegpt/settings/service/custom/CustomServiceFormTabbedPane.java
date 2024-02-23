package ee.carlrobert.codegpt.settings.service.custom;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

class CustomServiceFormTabbedPane extends JBTabbedPane {

  private final JBTable headersTable;
  private final JBTable bodyTable;

  CustomServiceFormTabbedPane(CustomServiceState customConfiguration) {
    headersTable = new JBTable(
        new DefaultTableModel(toArray(customConfiguration.getHeaders()),
            new Object[]{"Key", "Value"}));
    bodyTable = new JBTable(
        new DefaultTableModel(toArray(customConfiguration.getBody()),
            new Object[]{"Key", "Value"}));

    setTabComponentInsets(JBUI.insetsTop(8));
    addTab("Headers", createTablePanel(headersTable));
    addTab("Body", createTablePanel(bodyTable));
  }

  public void setEnabled(boolean enabled) {
    headersTable.setEnabled(enabled);
    bodyTable.setEnabled(enabled);
  }

  public void setHeaders(Map<String, String> headers) {
    setTableData(headersTable, headers);
  }

  public Map<String, String> getHeaders() {
    return getTableData(headersTable).entrySet().stream()
        .filter(entry -> entry.getKey() != null && entry.getValue() != null)
        .collect(toMap(Entry::getKey, entry -> (String) entry.getValue()));
  }

  public void setBody(Map<String, ?> body) {
    setTableData(bodyTable, body);
  }

  public Map<String, ?> getBody() {
    return getTableData(bodyTable);
  }

  private void setTableData(JBTable table, Map<String, ?> values) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0);

    for (var entry : values.entrySet()) {
      model.addRow(new Object[]{entry.getKey(), entry.getValue()});
    }
  }

  private Map<String, Object> getTableData(JBTable table) {
    var model = (DefaultTableModel) table.getModel();
    var data = new HashMap<String, Object>();
    for (int i = 0; i < model.getRowCount(); i++) {
      var key = (String) model.getValueAt(i, 0);
      data.put(key, model.getValueAt(i, 1));
    }
    return data;
  }

  public static Object[][] toArray(Map<?, ?> actionsMap) {
    return actionsMap.entrySet()
        .stream()
        .map((entry) -> new Object[]{entry.getKey(), entry.getValue()})
        .collect(toList())
        .toArray(new Object[0][0]);
  }

  private JPanel createTablePanel(JBTable table) {
    return ToolbarDecorator.createDecorator(table)
        .setAddAction(anActionButton ->
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{"", null}))
        .setRemoveAction(anActionButton ->
            ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow()))
        .disableUpAction()
        .disableDownAction()
        .createPanel();
  }
}
