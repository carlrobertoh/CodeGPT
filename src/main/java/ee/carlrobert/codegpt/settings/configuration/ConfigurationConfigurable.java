package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class ConfigurationConfigurable implements Configurable, Disposable {

  private ConfigurationComponent configurationComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("configurationConfigurable.displayName");
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var configuration = ConfigurationState.getInstance();
    configurationComponent = new ConfigurationComponent(this, configuration);
    return configurationComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var configuration = ConfigurationState.getInstance();
    return !configurationComponent.getTableData().equals(configuration.tableData) ||
        configurationComponent.getMaxTokens() != configuration.maxTokens ||
        configurationComponent.getTemperature() != configuration.temperature ||
        !configurationComponent.getSystemPrompt().equals(configuration.systemPrompt) ||
        configurationComponent.isCreateNewChatOnEachAction() != configuration.createNewChatOnEachAction;
  }

  @Override
  public void apply() {
    var configuration = ConfigurationState.getInstance();
    configuration.tableData = configurationComponent.getTableData();
    configuration.maxTokens = configurationComponent.getMaxTokens();
    configuration.temperature = configurationComponent.getTemperature();
    configuration.systemPrompt = configurationComponent.getSystemPrompt();
    configuration.createNewChatOnEachAction = configurationComponent.isCreateNewChatOnEachAction();
    EditorActionsUtil.refreshActions();
  }

  @Override
  public void reset() {
    var configuration = ConfigurationState.getInstance();
    configurationComponent.setTableData(configuration.tableData);
    configurationComponent.setMaxTokens(configuration.maxTokens);
    configurationComponent.setTemperature(configuration.temperature);
    configurationComponent.setSystemPrompt(configuration.systemPrompt);
    configurationComponent.setCreateNewChatOnEachAction(configuration.createNewChatOnEachAction);
    EditorActionsUtil.refreshActions();
  }

  @Override
  public void disposeUIResources() {
    configurationComponent = null;
  }

  @Override
  public void dispose() {
  }
}
