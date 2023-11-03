package ee.carlrobert.codegpt.completions.you.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.completions.you.YouApiClient;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Service
public final class YouAuthenticationService {

  private static final Logger LOG = Logger.getInstance(YouAuthenticationService.class);
  private static final YouAuthClient authClient = YouAuthClient.getInstance();

  private YouAuthenticationService() {
  }

  public static YouAuthenticationService getInstance() {
    return ApplicationManager.getApplication().getService(YouAuthenticationService.class);
  }

  public void signInAsync(String email, String password,
      AuthenticationHandler authenticationHandler) {
    authClient.authenticate(email, password, new AuthenticationCallback(authenticationHandler));
  }

  static class AuthenticationCallback implements Callback {

    private final AuthenticationHandler authenticationHandler;

    public AuthenticationCallback(AuthenticationHandler authenticationHandler) {
      this.authenticationHandler = authenticationHandler;
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
          var messageBus = ApplicationManager.getApplication().getMessageBus();
          var userManager = YouUserManager.getInstance();

          var authenticationResponse =
              new ObjectMapper().readValue(body.string(), YouAuthenticationResponse.class);
          userManager.setAuthenticationResponse(authenticationResponse);
          authenticationHandler.handleAuthenticated(authenticationResponse);

          var subscription =
              YouApiClient.getInstance().getSubscription(authenticationResponse);
          var subscribed = subscription != null && "youpro".equals(subscription.getService());
          userManager.setSubscribed(subscribed);
          if (subscribed) {
            messageBus.syncPublisher(YouSubscriptionNotifier.SUBSCRIPTION_TOPIC).subscribed();
          }

          messageBus
              .syncPublisher(AuthenticationNotifier.AUTHENTICATION_TOPIC)
              .authenticationSuccessful();
          return;
        } catch (IOException e) {
          throw new RuntimeException("Unable to deserialize session", e);
        }
      }

      try {
        authenticationHandler.handleError(
            new ObjectMapper().readValue(body.string(), YouAuthenticationError.class));
      } catch (Throwable ex) {
        authenticationHandler.handleGenericError();
        throw new RuntimeException(ex);
      }
    }
  }
}
