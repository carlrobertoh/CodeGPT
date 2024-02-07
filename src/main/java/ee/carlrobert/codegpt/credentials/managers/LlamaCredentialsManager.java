package ee.carlrobert.codegpt.credentials.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.CredentialsService;

@Service
public final class LlamaCredentialsManager extends ApiKeyCredentialsManager {

  public static final String PREFIX = "LLAMA";

  public LlamaCredentialsManager() {
    super(PREFIX);
  }

  public LlamaCredentialsManager(CredentialsService credentialsService) {
    super(PREFIX, credentialsService);
  }

  public static LlamaCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaCredentialsManager.class);
  }
}