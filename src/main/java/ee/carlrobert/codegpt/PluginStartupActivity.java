package ee.carlrobert.codegpt;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationHandler;
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationError;
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationService;
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;
import ee.carlrobert.codegpt.credentials.managers.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

  private static final Logger LOG = Logger.getInstance(PluginStartupActivity.class);

  @Override
  public void runActivity(@NotNull Project project) {
    EditorActionsUtil.refreshActions();
    var authenticationResponse = YouUserManager.getInstance().getAuthenticationResponse();
    if (authenticationResponse == null) {
      handleYouServiceAuthentication();
    }
  }

  private void handleYouServiceAuthentication() {
    var settings = GeneralSettingsState.getInstance();
    if (!settings.isPreviouslySignedIn()) {
      return;
    }

    var password = YouCredentialsManager.getInstance().getCredentials().getPassword();
    if (!settings.getEmail().isEmpty() && password != null && !password.isEmpty()) {
      YouAuthenticationService.getInstance()
          .signInAsync(settings.getEmail(), password, new AuthenticationHandler() {
            @Override
            public void handleAuthenticated(YouAuthenticationResponse authenticationResponse) {
              OverlayUtil.showNotification(
                  "Authentication successful.",
                  NotificationType.INFORMATION);
            }

            @Override
            public void handleGenericError() {
              OverlayUtil.showNotification(
                  "Something went wrong while trying to authenticate.",
                  NotificationType.ERROR);
            }

            @Override
            public void handleError(YouAuthenticationError youAuthenticationError) {
              OverlayUtil.showNotification(
                  youAuthenticationError.getErrorMessage(),
                  NotificationType.ERROR);
            }
          });
    }
  }
}
