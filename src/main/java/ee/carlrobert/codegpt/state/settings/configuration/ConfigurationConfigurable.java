package ee.carlrobert.codegpt.state.settings.configuration;

import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.action.ActionsUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class ConfigurationConfigurable implements Configurable {

  private ConfigurationComponent configurationComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeGPT: Configuration";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var configuration = ConfigurationState.getInstance();
    configurationComponent = new ConfigurationComponent(configuration);
    return configurationComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var configuration = ConfigurationState.getInstance();
    return !configurationComponent.getTableData().equals(configuration.tableData);
  }

  @Override
  public void apply() {
    var configuration = ConfigurationState.getInstance();
    configuration.tableData = configurationComponent.getTableData();
    ActionsUtil.refreshActions(configuration.tableData);
  }

  @Override
  public void reset() {
    var configuration = ConfigurationState.getInstance();
    configurationComponent.setTableData(configuration.tableData);
  }

  @Override
  public void disposeUIResources() {
    configurationComponent = null;
  }
}
