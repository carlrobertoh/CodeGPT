package ee.carlrobert.codegpt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionMessage;

@Service
public final class EncodingManager {

  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private Encoding encoding;

  private EncodingManager() {
    setEncoding(ModelSettingsState.getInstance().getCompletionModel());
  }

  public static EncodingManager getInstance() {
    return ApplicationManager.getApplication().getService(EncodingManager.class);
  }

  public void setEncoding(String modelName) {
    this.encoding = registry.getEncodingForModel(modelName).orElse(null);
  }

  public int countMessageTokens(OpenAIChatCompletionMessage message) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return encoding.countTokens(message.getRole() + message.getContent()) + tokensPerMessage;
  }

  public int countTokens(String text) {
    return encoding.countTokens(text);
  }
}
