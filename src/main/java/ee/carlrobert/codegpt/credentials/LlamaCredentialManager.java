package ee.carlrobert.codegpt.credentials;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class LlamaCredentialManager extends SingleCredentialManager {

  private LlamaCredentialManager() {
    super("LLAMA_API_KEY");
  }

  public static LlamaCredentialManager getInstance() {
    return ApplicationManager.getApplication().getService(LlamaCredentialManager.class);
  }
}
