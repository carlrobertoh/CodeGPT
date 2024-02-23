package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class CustomServiceCredentialManager extends SingleCredentialManager {

  private CustomServiceCredentialManager() {
    super("CUSTOM_SERVICE_API_KEY");
  }

  public static CustomServiceCredentialManager getInstance() {
    return ApplicationManager.getApplication().getService(CustomServiceCredentialManager.class);
  }
}
