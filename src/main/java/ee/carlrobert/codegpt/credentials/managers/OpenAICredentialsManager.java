package ee.carlrobert.codegpt.credentials.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.CredentialsService;

@Service
public final class OpenAICredentialsManager extends ApiKeyCredentialsManager {

  public static final String PREFIX = "OPENAI";

  public OpenAICredentialsManager() {
    super(PREFIX);
  }

  public OpenAICredentialsManager(CredentialsService credentialsService) {
    super(PREFIX, credentialsService);
  }

  public static OpenAICredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(OpenAICredentialsManager.class);
  }
}
