package ee.carlrobert.codegpt;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.action.ActionsUtil;
import ee.carlrobert.codegpt.client.ClientProvider;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.configuration.ConfigurationState;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    ActionsUtil.refreshActions(ConfigurationState.getInstance().tableData);
    var settings = SettingsState.getInstance();
    if (settings.useOpenAIAccountName) {
      ClientProvider.getDashboardClient()
          .getSubscriptionAsync(subscription ->
              settings.displayName = subscription.getAccountName());
    }
  }
}
