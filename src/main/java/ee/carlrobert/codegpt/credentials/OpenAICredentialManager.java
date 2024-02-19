package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class OpenAICredentialManager extends SingleCredentialManager {

  private OpenAICredentialManager() {
    super("OPENAI_API_KEY");
  }

  public static OpenAICredentialManager getInstance() {
    return ApplicationManager.getApplication().getService(OpenAICredentialManager.class);
  }
}
