package ee.carlrobert.codegpt.credentials.manager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class LlamaLocalCredentialsManager extends ApiKeyCredentialsManager {

  public LlamaLocalCredentialsManager() {
    super("LLAMA_LOCAL");
  }

  public static LlamaLocalCredentialsManager getInstance() {
    return ApplicationManager.getApplication()
        .getService(LlamaLocalCredentialsManager.class);
  }

}