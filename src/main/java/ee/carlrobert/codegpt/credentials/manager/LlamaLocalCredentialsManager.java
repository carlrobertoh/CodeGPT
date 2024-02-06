package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.CredentialsService;

@Service
public final class LlamaLocalCredentialsManager extends ApiKeyCredentialsManager {

  public static final String PREFIX = "LLAMA_LOCAL";

  public LlamaLocalCredentialsManager() {
    super(PREFIX);
  }

  public LlamaLocalCredentialsManager(CredentialsService credentialsService) {
    super(PREFIX, credentialsService);
  }

  public static LlamaLocalCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaLocalCredentialsManager.class);
  }

}