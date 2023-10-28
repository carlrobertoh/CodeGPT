package ee.carlrobert.codegpt.completions.you;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;

@Service
public final class YouUserManager {

  private YouAuthenticationResponse authenticationResponse;
  private boolean subscribed;

  private YouUserManager() {
  }

  public static YouUserManager getInstance() {
    return ApplicationManager.getApplication().getService(YouUserManager.class);
  }

  public YouAuthenticationResponse getAuthenticationResponse() {
    return authenticationResponse;
  }

  public void setAuthenticationResponse(YouAuthenticationResponse authenticationResponse) {
    this.authenticationResponse = authenticationResponse;
  }

  public void clearSession() {
    authenticationResponse = null;
    subscribed = false;

    ApplicationManager.getApplication().getMessageBus()
        .syncPublisher(SignedOutNotifier.SIGNED_OUT_TOPIC)
        .signedOut();
  }

  public void setSubscribed(boolean subscribed) {
    this.subscribed = subscribed;
  }

  public boolean isSubscribed() {
    return subscribed;
  }

  public boolean isAuthenticated() {
    return authenticationResponse != null;
  }
}
