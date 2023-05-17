package ee.carlrobert.codegpt;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.action.ActionsUtil;
import ee.carlrobert.codegpt.client.ClientProvider;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    ActionsUtil.refreshActions();
    var settings = SettingsState.getInstance();
    if (!settings.getApiKey().isEmpty() && settings.useOpenAIAccountName) {
      ClientProvider.getDashboardClient()
          .getSubscriptionAsync(subscription ->
              settings.displayName = subscription.getAccountName());
    }
  }
}
