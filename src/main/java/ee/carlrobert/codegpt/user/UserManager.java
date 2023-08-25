package ee.carlrobert.codegpt.user;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.user.auth.Session;
import ee.carlrobert.codegpt.user.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.user.subscription.Subscription;
import org.jetbrains.annotations.Nullable;

@Service
public final class UserManager {

  private Session session;
  private Subscription subscription;

  private UserManager() {
  }

  public static UserManager getInstance() {
    return ApplicationManager.getApplication().getService(UserManager.class);
  }

  public @Nullable Session getSession() {
    return session;
  }

  public void setSession(@Nullable Session session) {
    this.session = session;
  }

  public void clearSession() {
    session = null;
    subscription = null;

    ApplicationManager.getApplication().getMessageBus()
        .syncPublisher(SignedOutNotifier.SIGNED_OUT_TOPIC)
        .signedOut();
  }

  public @Nullable Subscription getSubscription() {
    return subscription;
  }

  public void setSubscription(@Nullable Subscription subscription) {
    this.subscription = subscription;
  }

  public boolean isSubscribed() {
    return session != null && subscription != null;
  }
}
