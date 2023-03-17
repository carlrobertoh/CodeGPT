package ee.carlrobert.codegpt.ide;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.ide.action.ActionsUtil;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import ee.carlrobert.codegpt.ide.settings.configuration.ConfigurationState;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    ConfigurationState cfgState = ConfigurationState.getInstance();
    ActionsUtil.refreshActions(cfgState.tableData);

    var settings = SettingsState.getInstance();
    var subscription = new ClientFactory().getClient().getSubscription();
    if (subscription != null) {
      settings.accountName = subscription.getAccountName();
    }
  }
}
