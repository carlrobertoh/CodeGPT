package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class ConfigurationConfigurable implements Configurable {

  private Disposable parentDisposable;

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
    parentDisposable = Disposer.newDisposable();
    configurationComponent = new ConfigurationComponent(parentDisposable, configuration);
    return configurationComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var configuration = ConfigurationState.getInstance();
    return !configurationComponent.getTableData().equals(configuration.getTableData())
        || configurationComponent.getMaxTokens() != configuration.getMaxTokens()
        || configurationComponent.getTemperature() != configuration.getTemperature()
        || !configurationComponent.getSystemPrompt().equals(configuration.getSystemPrompt())
        || configurationComponent.isCreateNewChatOnEachAction()
        != configuration.isCreateNewChatOnEachAction();
  }

  @Override
  public void apply() {
    var configuration = ConfigurationState.getInstance();
    configuration.setTableData(configurationComponent.getTableData());
    configuration.setMaxTokens(configurationComponent.getMaxTokens());
    configuration.setTemperature(configurationComponent.getTemperature());
    configuration.setSystemPrompt(configurationComponent.getSystemPrompt());
    configuration.setCreateNewChatOnEachAction(
        configurationComponent.isCreateNewChatOnEachAction());
    EditorActionsUtil.refreshActions();
  }

  @Override
  public void reset() {
    var configuration = ConfigurationState.getInstance();
    configurationComponent.setTableData(configuration.getTableData());
    configurationComponent.setMaxTokens(configuration.getMaxTokens());
    configurationComponent.setTemperature(configuration.getTemperature());
    configurationComponent.setSystemPrompt(configuration.getSystemPrompt());
    configurationComponent.setCreateNewChatOnEachAction(
        configuration.isCreateNewChatOnEachAction());
    EditorActionsUtil.refreshActions();
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    configurationComponent = null;
  }
}
