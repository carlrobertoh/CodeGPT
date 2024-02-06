package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.CredentialsService;

@Service
public final class LlamaRemoteCredentialsManager extends ApiKeyCredentialsManager {

  public static final String PREFIX = "LLAMA_REMOTE";

  public LlamaRemoteCredentialsManager() {
    super(PREFIX);
  }

  public LlamaRemoteCredentialsManager(CredentialsService credentialsService) {
    super(PREFIX, credentialsService);
  }

  public static LlamaRemoteCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaRemoteCredentialsManager.class);
  }
}