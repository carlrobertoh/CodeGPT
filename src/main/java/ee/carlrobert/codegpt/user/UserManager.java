package ee.carlrobert.codegpt.user;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.user.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.user.auth.response.AuthenticationResponse;

@Service
public final class UserManager {

  private AuthenticationResponse authenticationResponse;

  private UserManager() {
  }

  public static UserManager getInstance() {
    return ApplicationManager.getApplication().getService(UserManager.class);
  }

  public AuthenticationResponse getAuthenticationResponse() {
    return authenticationResponse;
  }

  public void setAuthenticationResponse(AuthenticationResponse authenticationResponse) {
    this.authenticationResponse = authenticationResponse;
  }

  public void clearSession() {
    authenticationResponse = null;

    ApplicationManager.getApplication().getMessageBus()
        .syncPublisher(SignedOutNotifier.SIGNED_OUT_TOPIC)
        .signedOut();
  }

  public boolean isSubscribed() {
    return true; // TODO
  }

  public boolean isAuthenticated() {
    return authenticationResponse != null;
  }
}
