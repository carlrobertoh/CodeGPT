package ee.carlrobert.codegpt.user.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.credentials.UserCredentialsManager;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.user.ApiClient;
import ee.carlrobert.codegpt.user.UserManager;
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

  public void refreshToken() {
    var userManager = UserManager.getInstance();
    var session = userManager.getSession();

    if (session == null) {
      throw new IllegalStateException("Tried to revalidate unauthenticated user");
    }

    client.refreshToken(session.getRefreshToken(), new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        userManager.clearSession();
      }

      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) {
        if (response.code() == 200) {
          var body = response.body();
          if (body != null) {
            try {
              handleSuccessfulAuthentication(new ObjectMapper().readValue(body.string(), Session.class));
              return;
            } catch (IOException e) {
              throw new RuntimeException("Unable to deserialize session", e);
            }
          }
        }

        userManager.clearSession();
        throw new RuntimeException("Internal server error. " + response.message());
      }
    });
  }

  private void handleSuccessfulAuthentication(Session session) {
    SettingsState.getInstance().previouslySignedIn = true;

    var userManager = UserManager.getInstance();
    userManager.setSession(session);

    var subscription = client.getSubscription(session.getAccessToken());
    if (subscription != null) {
      userManager.setSubscription(subscription);
    }
    ApplicationManager.getApplication().getMessageBus()
        .syncPublisher(AuthenticationNotifier.AUTHENTICATION_TOPIC)
        .authenticationSuccessful();
  }

  class AuthenticationCallback implements Callback {

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
      if (response.code() == 401) {
        authenticationHandler.handleInvalidCredentials();
      }
      if (response.code() == 200) {
        var body = response.body();
        if (body != null) {
          try {
            var session = new ObjectMapper().readValue(body.string(), Session.class);
            handleSuccessfulAuthentication(session);
            SettingsState.getInstance().email = email;
            UserCredentialsManager.getInstance().setAccountPassword(password);
            authenticationHandler.handleAuthenticated();
            return;
          } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize session", e);
          }
        }
      }

      authenticationHandler.handleGenericError();
      throw new RuntimeException("Internal server error. " + response.message());
    }
  }
}
