package ee.carlrobert.codegpt.user.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.credentials.UserCredentialsManager;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.user.ApiClient;
import ee.carlrobert.codegpt.user.UserManager;
import ee.carlrobert.codegpt.user.auth.response.AuthenticationResponse;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Service
public final class AuthenticationService {

  private static final Logger LOG = Logger.getInstance(AuthenticationService.class);
  private static final ApiClient client = ApiClient.getInstance();

  private AuthenticationService() {
  }

  public static AuthenticationService getInstance() {
    return ApplicationManager.getApplication().getService(AuthenticationService.class);
  }

  public void signInAsync(String email, String password, AuthenticationHandler authenticationHandler) {
    client.authenticate(email, password, new AuthenticationCallback(authenticationHandler, email, password));
  }

  static class AuthenticationCallback implements Callback {

    private final AuthenticationHandler authenticationHandler;
    private final String email;
    private final String password;

    public AuthenticationCallback(AuthenticationHandler authenticationHandler, String email, String password) {
      this.authenticationHandler = authenticationHandler;
      this.email = email;
      this.password = password;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
      OverlayUtils.showNotification("Authentication failed.", NotificationType.ERROR);
      LOG.error("Unable to retrieve session", e);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
      var body = response.body();
      if (body == null) {
        authenticationHandler.handleGenericError();
        return;
      }

      if (response.code() == 200) {
        try {
          SettingsState.getInstance().setEmail(email);
          UserCredentialsManager.getInstance().setAccountPassword(password);

          var authenticationResponse = new ObjectMapper().readValue(body.string(), AuthenticationResponse.class);
          UserManager.getInstance().setAuthenticationResponse(authenticationResponse);
          authenticationHandler.handleAuthenticated(authenticationResponse);

          ApplicationManager.getApplication().getMessageBus()
              .syncPublisher(AuthenticationNotifier.AUTHENTICATION_TOPIC)
              .authenticationSuccessful();
          return;
        } catch (IOException e) {
          throw new RuntimeException("Unable to deserialize session", e);
        }
      }

      try {
        authenticationHandler.handleError(new ObjectMapper().readValue(body.string(), AuthenticationError.class));
      } catch (Throwable ex) {
        authenticationHandler.handleGenericError();
        throw new RuntimeException(ex);
      }
    }
  }
}
