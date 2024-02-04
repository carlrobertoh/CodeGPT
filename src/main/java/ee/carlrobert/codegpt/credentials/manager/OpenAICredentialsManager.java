package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class OpenAICredentialsManager extends ApiKeyCredentialsManager {

  public OpenAICredentialsManager() {
    super("OPEN_AI");
  }

  public static OpenAICredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(OpenAICredentialsManager.class);
  }
}
