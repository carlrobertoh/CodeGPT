package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class AnthropicCredentialsManager extends SingleCredentialManager {

  private AnthropicCredentialsManager() {
    super("ANTHROPIC_API_KEY");
  }

  public static AnthropicCredentialsManager getInstance() {
    return ApplicationManager.getApplication().getService(AnthropicCredentialsManager.class);
  }
}