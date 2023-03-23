package ee.carlrobert.codegpt;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.account.AccountDetailsState;
import ee.carlrobert.codegpt.action.ActionsUtil;
import ee.carlrobert.codegpt.client.ClientProvider;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    ActionsUtil.refreshActions(ConfigurationState.getInstance().tableData);
    var accountDetails = AccountDetailsState.getInstance();
    if ("User".equals(accountDetails.accountName) || accountDetails.accountName == null) {
      ClientProvider.getBillingClient()
          .getSubscriptionAsync(subscription ->
              accountDetails.accountName = subscription.getAccountName());
    }
  }
}
