package ee.carlrobert.codegpt;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;

public class EncodingManager {

  private static final EncodingManager instance = new EncodingManager();
  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private Encoding encoding;

  private EncodingManager() {
    var settings = SettingsState.getInstance();
    setEncoding(settings.isChatCompletionOptionSelected ? settings.chatCompletionBaseModel : settings.textCompletionBaseModel);
  }

  public static EncodingManager getInstance() {
    return instance;
  }

  public void setEncoding(String modelName) {
    this.encoding = registry.getEncodingForModel(modelName).orElseThrow();
  }

  public int countMessageTokens(ChatCompletionMessage message) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return encoding.countTokens(message.getRole() + message.getContent()) + tokensPerMessage;
  }
}
