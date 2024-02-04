package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class LlamaRemoteCredentialsManager extends ApiKeyCredentialsManager {

  public LlamaRemoteCredentialsManager() {
    super("LLAMA_REMOTE");
  }

  public static LlamaRemoteCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaRemoteCredentialsManager.class);
  }
}