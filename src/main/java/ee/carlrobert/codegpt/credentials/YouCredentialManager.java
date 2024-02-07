package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class YouCredentialManager extends SingleCredentialManager {

  private YouCredentialManager() {
    super("YOU_ACCOUNT_PASSWORD");
  }

  public static YouCredentialManager getInstance() {
    return ApplicationManager.getApplication().getService(YouCredentialManager.class);
  }

}
