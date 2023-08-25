package ee.carlrobert.codegpt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;

@Service
public final class EncodingManager {

  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private Encoding encoding;

  private EncodingManager() {
    var settings = SettingsState.getInstance();
    setEncoding(settings.isChatCompletionOptionSelected ? settings.chatCompletionBaseModel : settings.textCompletionBaseModel);
  }

  public static EncodingManager getInstance() {
    return ApplicationManager.getApplication().getService(EncodingManager.class);
  }

  public void setEncoding(String modelName) {
    this.encoding = registry.getEncodingForModel(modelName).orElseThrow();
  }

  public int countMessageTokens(ChatCompletionMessage message) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return encoding.countTokens(message.getRole() + message.getContent()) + tokensPerMessage;
  }

  public int countTokens(String text) {
    return encoding.countTokens(text);
  }
}
